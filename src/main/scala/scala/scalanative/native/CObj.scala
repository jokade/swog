package scala.scalanative.native

import de.surfice.smacrotools.MacroAnnotationHandler

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.reflect.macros.whitebox
import scala.language.experimental.macros

@compileTimeOnly("enable macro paradise to expand macro annotations")
class CObj(prefix: String = null, newSuffix: String = null, semantics: CObj.Semantics = null) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro CObj.Macro.impl
}

object CObj {

  trait CRef[T] {
    def __ref: Ptr[T]
  }

  trait CRefVoid extends CRef[Byte]

  sealed trait Semantics
  case object Wrapped extends Semantics
  case object Raw extends Semantics

  private[native] class Macro(val c: whitebox.Context) extends MacroAnnotationHandler {
    import c.universe._

    override def annotationName = "CObj"
    override def supportsClasses: Boolean = true
    override def supportsTraits: Boolean = true
    override def supportsObjects: Boolean = true
    override def createCompanion: Boolean = true

    private val tPtrByte = weakTypeOf[Ptr[Byte]]
    private val tByte = weakTypeOf[Byte]
    private val tCRef = weakTypeOf[CRef[_]]
    private val tCRefVoid = weakTypeOf[CRefVoid]
    private val selfPtr = q"val self: $tPtrByte"
    private val refPtr = q"__ref: $tPtrByte"
    private val expExtern = q"scalanative.native.extern"

    private val annotationParamNames = Seq("prefix","newSuffix","semantics")

    implicit class MacroData(data: Map[String,Any]) {
      type Data = Map[String,Any]
      type Externals = Map[String,(String,Tree)]
      def externalPrefix: String = data.getOrElse("externalPrefix","").asInstanceOf[String]
      def externals: Externals = data.getOrElse("externals", Map()).asInstanceOf[Externals]
      def constructors: Seq[(String,Seq[ValDef])] = data.getOrElse("constructors",Nil).asInstanceOf[Seq[(String,Seq[ValDef])]]
      def semantics: Semantics = data.getOrElse("semantics",Wrapped).asInstanceOf[Semantics]
      def newSuffix: String = data.getOrElse("newSuffix","").asInstanceOf[String]
      def crefType: Type = data.getOrElse("crefType",tCRefVoid).asInstanceOf[Type]
      def withExternalPrefix(prefix: String): Data = data.updated("externalPrefix",prefix)
      def withExternals(externals: Externals): Data = data.updated("externals",externals)
      def withConstructors(ctors: Seq[(String,Seq[Tree])]): Data = data.updated("constructors",ctors)
      def withSemantics(semantics: Semantics): Data = data.updated("semantics",semantics)
      def withNewSuffix(suffix: String): Data = data.updated("newSuffix",suffix)
      def withCRefType(tpe: Type): Data = data.updated("crefType",tpe)
    }

    override def analyze: Analysis = super.analyze andThen {
      case (cls: ClassParts, data) =>
        val updData = (
          analyzeMainAnnotation(cls) _
            andThen analyzeTypes(cls) _
            andThen analyzeConstructor(cls) _
            andThen analyzeBody(cls) _
          )(data)
        (cls, updData)
      case (obj: ObjectParts, data) =>
        val updData = (
          analyzeMainAnnotation(obj) _
          andThen analyzeBody(obj) _
        )(data)
        (obj, updData)
      case default => default
    }

    override def transform: Transformation = super.transform andThen {
      /* transform class */
      case cls: ClassTransformData =>
        val transformedBody = genTransformedTypeBody(cls)
        cls
          .updBody(transformedBody)
          .updCtorParams(genTransformedCtorParams(cls))
          .updCtorMods(Modifiers(Flag.PROTECTED)) // ensure that the class can't be instantiated using new
      /* transform trait */
      case trt: TraitTransformData =>
        val transformedBody = genTransformedTypeBody(trt)
        trt.updBody(transformedBody)
      /* transform companion object */
      case obj: ObjectTransformData =>
        val transformedBody = genTransformedCompanionBody(obj) :+ genBindingsObject(obj.data)
        obj.updBody(transformedBody)
      case default => default
    }


    private def analyzeMainAnnotation(tpe: CommonParts)(data: Data): Data = {
      val annotParams = extractAnnotationParameters(c.prefix.tree, annotationParamNames)
      val externalPrefix = annotParams("prefix") match {
        case Some(prefix) => extractStringConstant(prefix).get
        case None => genPrefixName(tpe.nameString)
      }
      val semantics = annotParams("semantics") match {
        case Some(Select(_,name)) => name.toString match {
          case "Raw" => Raw
          case "Wrapped" => Wrapped
        }
        case None => Wrapped
      }
      val newSuffix = annotParams("newSuffix") match {
        case Some(suffix) => extractStringConstant(suffix).get
        case None => "new"
      }
      data.withExternalPrefix(externalPrefix).withSemantics(semantics).withNewSuffix(newSuffix)
    }

    private def analyzeTypes(tpe: TypeParts)(data: Data): Data = {
      val crefType = tpe.parents.map(getType(_)).filter( _ <:< tCRef ) match {
        case Nil => tByte
        case List(tpe@TypeRef(_,sym,args)) if sym.toString == "trait CRef" =>
          args.head
        case List(tpe) =>
          c.error(c.enclosingPosition,"CObj types can only directly extend CObj.CRef[T]")
          ???
        case types =>
          c.error(c.enclosingPosition,s"CObj types can't extend more than one instance of CObj.CRef (found: $types)")
          ???
      }
      data.withCRefType(crefType)
    }


    private def analyzeConstructor(cls: ClassParts)(data: Data): Data = {
      data.withConstructors( Seq( (genExternalName(data.externalPrefix,data.newSuffix),cls.params.asInstanceOf[List[ValDef]]) ) )
    }

    private def analyzeBody(tpe: CommonParts)(data: Data): Data = {
      val prefix = data.externalPrefix
      val typeExternals = tpe.body.collect {
        case t@DefDef(mods, name, types, args, rettype, rhs) if isExtern(rhs) =>
          genExternalBinding(prefix,t,!tpe.isObject,data)
      }
      val companionExternals = tpe match {
        case t: TypeParts => t.companion.map(_.body.collect {
          case t@DefDef(mods, name, types, args, rettype, rhs) if isExtern(rhs) =>
            genExternalBinding(prefix,t,false,data)
        }).getOrElse(Map())
        case _ => Nil
      }
      data.withExternals( (typeExternals ++ companionExternals).toMap )
    }

    private def genTransformedCtorParams(cls: ClassTransformData): Seq[Tree] = cls.data.semantics match {
      case Wrapped => Seq(q"val __ref: scalanative.native.Ptr[${cls.data.crefType}]")
      case Raw => cls.modParts.params
    }

    private def genTransformedTypeBody(t: TypeTransformData[TypeParts]): Seq[Tree] = {
      val companion = t.modParts.companion.get.name
      val imports = Seq(q"import $companion.__ext")
      val ctors = genSecondaryConstructor(t)
      imports ++ ctors ++ (t.modParts.body map {
        case tree @ DefDef(mods, name, types, args, rettype, rhs) if isExtern(rhs) =>
          val externalName = t.data.externals(name.toString)._1
          genExternalCall(externalName,tree,false,t.data.semantics)
        case default => default
      })
    }

    private def genSecondaryConstructor(t: TypeTransformData[TypeParts]): Seq[Tree] = {
      val companion = t.modParts.companion.get.name
      t.data.semantics match {
        case Wrapped =>
          t.data.constructors.map { p =>
            val args = transformExternalCallArgs(p._2)
            DefDef(Modifiers(),
              termNames.CONSTRUCTOR,
              List(),
              List(p._2.toList),
              TypeTree(),
              Block(
                Nil,
                Apply(Ident(termNames.CONSTRUCTOR), List(q"$companion.__ext.${TermName(p._1)}(..$args)"))
              ))
          }
        case Raw => Nil
      }
    }

    private def genTransformedCompanionBody(t: TransformData[CommonParts]): Seq[Tree] = {
      t.modParts.body map {
        case tree @ DefDef(mods, name, types, args, rettype, rhs) if isExtern(rhs) =>
          val externalName = t.data.externals(name.toString)._1
          genExternalCall(externalName,tree,t.modParts.isObject,t.data.semantics)
        case default => default
      }
    }

    private def genBindingsObject(data: MacroData): Tree = {
      val ctors = data.constructors.map{
        case (externalName,args) => q"def ${TermName(externalName)}(..$args): scalanative.native.Ptr[${data.crefType}] = $expExtern"
      }
      val defs = data.externals.values.map(_._2)
      q"""@scalanative.native.extern object __ext {..${ctors++defs}}"""
    }


    private def genExternalBinding(prefix: String, scalaDef: DefDef, isInstanceMethod: Boolean, data: Data): (String,(String,Tree)) = {
      val scalaName = scalaDef.name.toString
//      val externalName = scalaName match {
//        case "apply" => prefix+"new"
//        case _ => genExternalName(prefix,scalaName)
//      }
      val externalName = genExternalName(prefix,scalaName)
      val externalParams =
        if(isInstanceMethod) scalaDef.vparamss match {
          case List(params) => List(q"val self: scalanative.native.Ptr[${data.crefType}]" +: params)
          case _ =>
            c.error(c.enclosingPosition,"methods with multiple parameter lists are not supported for @CObj classes")
            ???
        }
        else scalaDef.vparamss
      val externalDef = DefDef(scalaDef.mods,TermName(externalName),scalaDef.tparams,externalParams,scalaDef.tpt,scalaDef.rhs)

      (scalaName,(externalName,externalDef))
    }

    private def genExternalCall(externalName: String, scalaDef: DefDef, isClassMethod: Boolean, semantics: Semantics): DefDef = {
      import scalaDef._
//      val args = transformExternalCallArgs(vparamss.head)
      val args = paramNames(vparamss.head)
      val external = TermName(externalName)
      val call =
        if(isClassMethod) q"__ext.$external(..$args)"
        else semantics match {
          case Raw => q"__ext.$external(this.cast[$tPtrByte],..$args)"
          case Wrapped => q"__ext.$external(__ref,..$args)"
        }
      DefDef(mods,name,tparams,vparamss,tpt,call)
    }

    private def transformExternalCallArgs(args: Seq[ValDef]): Seq[Tree] = {
      args map {
        // TODO: currently crashes due to https://github.com/scala-native/scala-native/issues/1142
        case ValDef(_,name,AppliedTypeTree(tpe,_),_) if tpe.toString == "_root_.scala.<repeated>" => q"$name:_*"
        case ValDef(_,name,_,_) => q"$name"
      }
    }

    def genExternalName(prefix: String, scalaName: String): String =
      prefix + scalaName.replaceAll("([A-Z])","_$1").toLowerCase

    def genPrefixName(clsName: String): String =
      clsName.replaceAll("(.)([A-Z])","$1_$2").toLowerCase + "_"


    private def isExtern(rhs: Tree): Boolean = rhs match {
      case Ident(TermName(id)) =>
        id == "extern"
      case Select(_,name) =>
        name.toString == "extern"
      case x =>
        false
    }

  }
}