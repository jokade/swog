package scala.scalanative.cobj

import de.surfice.smacrotools.MacroAnnotationHandler

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.language.experimental.macros
import scala.reflect.macros.{TypecheckException, whitebox}
import scala.scalanative.cobj.runtime.CObjObject
import scala.scalanative.unsafe.Ptr

@compileTimeOnly("enable macro paradise to expand macro annotations")
class CObj(prefix: String = null, newSuffix: String = null, namingConvention: NamingConvention.Value = NamingConvention.SnakeCase) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro CObj.Macro.impl
}

object CObj {

  class CObjWrapper extends StaticAnnotation

  private[cobj] class Macro(val c: whitebox.Context) extends MacroBase {

    def isMutable: Boolean = false
    override def annotationName = "scala.scalanative.native.cobj.CObj"
    override def supportsClasses: Boolean = true
    override def supportsTraits: Boolean = true
    override def supportsObjects: Boolean = true
    override def createCompanion: Boolean = true

  }


  private[cobj] abstract class MacroBase extends MacroAnnotationHandler {
    val c: whitebox.Context

    import c.universe._

    def isMutable: Boolean

    private val tPtrByte = weakTypeOf[Ptr[Byte]]
    private val tAnyRef = weakTypeOf[AnyRef]
    private val tCObjObject = weakTypeOf[CObjObject]
    private val tCObjWrapperAnnotation = weakTypeOf[CObjWrapper]
    private val expExtern = q"scalanative.unsafe.extern"
    private val cobjWrapperAnnotation = q"new scalanative.cobj.CObj.CObjWrapper"

    private val annotationParamNames = Seq("prefix","newSuffix","namingConvention","semantics")

    implicit class MacroData(data: Map[String,Any]) {
      type Data = Map[String, Any]
      type Externals = Map[String, (String, Tree)]
      type Statements = Seq[Tree]

      def externalPrefix: String = data.getOrElse("externalPrefix", "").asInstanceOf[String]
      def withExternalPrefix(prefix: String): Data = data.updated("externalPrefix",prefix)

      def newSuffix: String = data.getOrElse("newSuffix","").asInstanceOf[String]
      def withNewSuffix(suffix: String): Data = data.updated("newSuffix",suffix)

      def namingConvention: NamingConvention.Value = data.getOrElse("namingConvention",NamingConvention.SnakeCase).asInstanceOf[NamingConvention.Value]
      def withNamingConvention(nc: NamingConvention.Value): Data = data.updated("namingConvention",nc)

      def isAbstract: Boolean = data.getOrElse("isAbstract",false).asInstanceOf[Boolean]
      def withIsAbstract(flag: Boolean): Data = data.updated("isAbstract",flag)

      def constructors: Seq[(String,Seq[ValDef])] = data.getOrElse("constructors",Nil).asInstanceOf[Seq[(String,Seq[ValDef])]]
      def withConstructors(ctors: Seq[(String,Seq[Tree])]): Data = data.updated("constructors",ctors)

      def externals: Externals = data.getOrElse("externals", Map()).asInstanceOf[Externals]
      def withExternals(externals: Externals): Data = data.updated("externals",externals)

      def currentType: String = data.getOrElse("currentType","").asInstanceOf[String]
      def withCurrentType(tpe: String): Data = data.updated("currentType",tpe)

      def parentIsCObj: Boolean = data.getOrElse("parentIsCObj",false).asInstanceOf[Boolean]
      def withParentIsCObj(flag: Boolean): Data = data.updated("parentIsCObj",flag)

      def additionalCompanionStmts: Statements = data.getOrElse("compStmts", Nil).asInstanceOf[Statements]
      def withAdditionalCompanionStmts(stmts: Statements): Data = data.updated("compStmts",stmts)
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
      case (trt: TraitParts, data) =>
        val updData = (
          analyzeMainAnnotation(trt) _
            andThen analyzeTypes(trt) _
            andThen analyzeBody(trt) _
          )(data)
        (trt, updData)
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
          .addAnnotations(cobjWrapperAnnotation)
          .updCtorParams(genTransformedCtorParams(cls))
          .updParents(genTransformedParents(cls))
      /* transform trait */
      case trt: TraitTransformData =>
        val transformedBody = genTransformedTypeBody(trt)
        trt
          .updBody(transformedBody)
          .addAnnotations(cobjWrapperAnnotation)
          .updParents(genTransformedParents(trt))
      /* transform companion object */
      case obj: ObjectTransformData =>
        val transformedBody = genTransformedCompanionBody(obj) ++ obj.data.additionalCompanionStmts :+ genBindingsObject(obj.data)
        obj.updBody(transformedBody)
      case default => default
    }

    private def analyzeMainAnnotation(tpe: CommonParts)(data: Data): Data = {
      val annotParams = extractAnnotationParameters(c.prefix.tree, annotationParamNames)
      val externalPrefix = annotParams("prefix") match {
        case Some(prefix) => extractStringConstant(prefix).get
        case None => genPrefixName(tpe.nameString)
      }
      val newSuffix = annotParams("newSuffix") match {
        case Some(suffix) => extractStringConstant(suffix).get
        case None => "new"
      }
      val namingConvention = annotParams("namingConvention") match {
        case Some(Select(_,name)) => NamingConvention.withName(name.toString)
        case None => NamingConvention.SnakeCase
      }
      data.withExternalPrefix(externalPrefix).withNewSuffix(newSuffix).withNamingConvention(namingConvention)
    }

    // TODO: move all checks (isCRef, isAbstract, ... in here and store the results in data)
    private def analyzeTypes(tpe: TypeParts)(data: Data): Data = {
      val isAbstract = tpe.modifiers.hasFlag(Flag.ABSTRACT)
//      val crefType = tpe.parents.map(getType(_)).filter( _ <:< tCRef ) match {
//        case Nil => tByte
//        case List(tpe@TypeRef(_,sym,args)) if sym.toString == "trait CRef" =>
//          args.head
//        case List(tpe) =>
//          c.error(c.enclosingPosition,"CObj types can only directly extend CRef[T]")
//          ???
//        case types =>
//          c.error(c.enclosingPosition,s"CObj types can't extend more than one instance of CRef (found: $types)")
//          ???
//      }
      val parentIsCObj = isExternalObject(tpe.parents.head,data)
//      val parentIsAbstract = getType(tpe.parents.head).typeSymbol.isAbstract
      data
        .withIsAbstract(isAbstract)
        .withCurrentType(tpe.fullName)
        .withParentIsCObj(parentIsCObj)
//        .withCRefType(crefType)
//        .withParentIsCRef(parentIsCRef)
//        .withParentIsAbstract(parentIsAbstract)
//        .withFullName(tpe.fullName)
    }

    private def analyzeConstructor(cls: ClassParts)(data: Data): Data = {
      val companionStmts =
        if (cls.isClass && !cls.modifiers.hasFlag(Flag.ABSTRACT))
          List(genWrapperImplicit(cls.name, cls.tparams))
        else
          Nil
      data
//        .withConstructors( Seq( (genExternalName(data.externalPrefix,data.newSuffix,data.namingConvention),cls.params.asInstanceOf[List[ValDef]]) ) )
        .withAdditionalCompanionStmts(data.additionalCompanionStmts ++ companionStmts)
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

    private def genTransformedParents(cls: TypeTransformData[TypeParts]): Seq[Tree] = {
      cls.modParts.parents map (p => (p,getType(p,true))) map {
        case (tree,tpe) if tpe =:= tAnyRef => tq"$tCObjObject"
        case (tree,tpe) if tpe =:= tCObjObject || tpe.typeSymbol.isAbstract => tree
        case (tree,tpe) if tpe <:< tCObjObject => q"$tree(__ptr)"
        case (tree,_) => tree
      }
    }

    private def genTransformedCtorParams(cls: ClassTransformData): Seq[Tree] =
//      if (cls.data.isAbstract || isMutable)
//        cls.modParts.params
//      else if(cls.data.parentIsCRef)
//        Seq(q"override val __ref: scalanative.native.cobj.Ref[${cls.data.crefType}]")
//      else
    if(cls.data.parentIsCObj)
      Seq(q"override val __ptr: $tPtrByte")
    else
      Seq(q"val __ptr: $tPtrByte")

    private def genTransformedTypeBody(t: TypeTransformData[TypeParts]): Seq[Tree] = {
      val companion = t.modParts.companion.get.name
      val imports = Seq(q"import $companion.__ext")
      val ctors = genSecondaryConstructor(t)
//        if(isMutable)
//          Nil
//        else if(isAbstract(t))
//          Seq(q"def __ref: scalanative.native.cobj.Ref[${t.data.crefType}]")
//        else
//          genSecondaryConstructor(t)
//      val ctors = Seq(s"val __ref: ${t.data.crefType} = ")
      imports ++ ctors ++ (t.modParts.body map {
        case tree @ DefDef(mods, name, types, args, rettype, rhs) if isExtern(rhs) =>
          val externalName = t.data.externals(name.toString)._1
          genExternalCall(externalName,tree,false,t.data)
        case default => default
      })
    }

    private def genTransformedCompanionBody(t: TransformData[CommonParts]): Seq[Tree] = {
      t.modParts.body map {
        case tree @ DefDef(mods, name, types, args, rettype, rhs) if isExtern(rhs) =>
          val externalName = t.data.externals(name.toString)._1
          genExternalCall(externalName,tree,t.modParts.isObject,t.data)
        case default => default
      }
    }

    private def genBindingsObject(data: MacroData): Tree = {
      val ctors = data.constructors.map{
        case (externalName,args) => q"def ${TermName(externalName)}(..$args): $tPtrByte = $expExtern"
      }
      val defs = data.externals.values.map(_._2)
      q"""@scalanative.unsafe.extern object __ext {..${ctors++defs}}"""
    }

    private def genWrapperImplicit(tpe: TypeName, tparams: Seq[Tree]): Tree =
      tparams.size match {
        case 0 =>
          q"""implicit object __wrapper extends scalanative.cobj.CObjWrapper[$tpe] {
            def wrap(ptr: scalanative.unsafe.Ptr[Byte]) = new $tpe(ptr)
            def unwrap(value: $tpe): scalanative.unsafe.Ptr[Byte] = value.__ptr
          }
          """
        case 1 =>
          q"""implicit object __wrapper extends scalanative.cobj.CObjWrapper[$tpe[_]] {
            def wrap(ptr: scalanative.unsafe.Ptr[Byte]) = new $tpe(ptr)
            def unwrap(value: $tpe[_]): scalanative.unsafe.Ptr[Byte] = value.__ptr
          }
          """
        case 2 =>
          q"""implicit object __wrapper extends scalanative.cobj.CObjWrapper[$tpe[_,_]] {
            def wrap(ptr: scalanative.unsafe.Ptr[Byte]) = new $tpe(ptr)
            def unwrap(value: $tpe[_,_]): scalanative.unsafe.Ptr[Byte] = value.__ptr
          }
          """
      }

    private def genSecondaryConstructor(t: TypeTransformData[TypeParts]): Seq[Tree] = {
      val companion = t.modParts.companion.get.name
      t.data.constructors.map { p =>
        val args = transformExternalCallArgs(p._2,t.data)
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
    }

    private def genPrefixName(clsName: String): String =
      clsName.replaceAll("(.)([A-Z])","$1_$2").toLowerCase + "_"

    private def genExternalName(prefix: String, scalaName: String, nc: NamingConvention.Value): String = nc match {
      case NamingConvention.SnakeCase =>
        prefix + scalaName.replaceAll("([A-Z])","_$1").toLowerCase
      case NamingConvention.PascalCase =>
        prefix + scalaName.head.toUpper + scalaName.tail
      case _ => prefix + scalaName
    }

    private def genExternalBinding(prefix: String, scalaDef: DefDef, isInstanceMethod: Boolean, data: Data): (String,(String,Tree)) = {
      val scalaName = scalaDef.name.toString
      val externalName = genExternalName(prefix,scalaName,data.namingConvention)
      val externalParams =
        if(isInstanceMethod) scalaDef.vparamss match {
          case Nil => List(List(q"val self: $tPtrByte"))
          case List(params) => List(q"val self: $tPtrByte" +: transformExternalBindingParams(params,data) )
          case List(inparams,outparams) =>
            List( q"val self: $tPtrByte" +:
              (transformExternalBindingParams(inparams,data) ++ transformExternalBindingParams(outparams,data,true)) )
          case _ =>
            c.error(c.enclosingPosition,"extern methods with more than two parameter lists are not supported for @CObj classes")
            ???
        }
        else scalaDef.vparamss match {
          case Nil => List(Nil)
          case List(params) => List(transformExternalBindingParams(params,data))
          case List(inparams,outparams) => List(transformExternalBindingParams(inparams++outparams,data))
          case x =>
            c.error(c.enclosingPosition,"extern methods with more than two parameter lists are not supported for @CObj classes")
            ???
        }
      val tpt =
        if(isExternalObject(scalaDef.tpt,data)) tq"scalanative.unsafe.Ptr[Byte]"
        else scalaDef.tpt
      val mods = Modifiers(NoFlags,scalaDef.mods.privateWithin,scalaDef.mods.annotations) // remove flags (e.g. 'override')
      val externalDef = DefDef(mods,TermName(externalName),scalaDef.tparams,externalParams,tpt,scalaDef.rhs)

      (scalaName,(externalName,externalDef))
    }

    private def transformExternalBindingParams(params: List[ValDef], data: Data, outParams: Boolean = false): List[ValDef] = {
      params map {
        case ValDef(mods,name,tpt,rhs) if isExternalObject(tpt,data) =>
          ValDef(mods,name,q"$tPtrByte",rhs)
        case default => default
      }
    }

    private def genExternalCall(externalName: String, scalaDef: DefDef, isClassMethod: Boolean, data: Data): DefDef = {
      import scalaDef._
      val args = vparamss match {
        case Nil => None
        case List(args) => Some(transformExternalCallArgs(args,data))
        case List(inargs,outargs) =>
          Some( transformExternalCallArgs(inargs,data) ++ transformExternalCallArgs(outargs,data) )
        case _ =>
          c.error(c.enclosingPosition,"extern methods with more than two parameter lists are not supported for @CObj classes")
          ???
      }
      val external = TermName(externalName)
      val call = args match {
        case Some(as) if isClassMethod => q"__ext.$external(..$as)"
        case Some(as) => q"__ext.$external(__ptr,..$as)"
        case None if isClassMethod => q"__ext.$external"
        case None => q"__ext.$external(__ptr)"
      }
      val rhs =
//        if(returnsThis(scalaDef))
//          q"$call;this"
//        else if(updatesThis(scalaDef))
//          q"this.__ref = $call.cast[$tRefNothing];this"
//        else
          wrapExternalCallResult(call,tpt,data,nullable(scalaDef))

      DefDef(mods,name,tparams,vparamss,tpt,rhs)
    }

    private def transformExternalCallArgs(args: Seq[ValDef], data: Data, outArgs: Boolean = false): Seq[Tree] = {
      args map {
        // TODO: currently crashes due to https://github.com/scala-native/scala-native/issues/1142
        case ValDef(_,name,tpt,_) if isExternalObject(tpt,data) || outArgs =>
          //q"$name.__ref.cast[$tPtrByte]"
          q"""if($name==null) null else $name.__ptr"""
        case ValDef(_,name,AppliedTypeTree(tpe,_),_) if tpe.toString == "_root_.scala.<repeated>" => q"$name:_*"
        case ValDef(_,name,tpt,_) => q"$name"
      }
    }

    private def wrapExternalCallResult(tree: Tree, tpt: Tree, data: Data, isNullable: Boolean): Tree = {
      if (isExternalObject(tpt,data)) {
        if(isNullable)
          q"""val res = $tree; if(res == null) null else new $tpt(res)"""
        else
          q"""new $tpt($tree)"""
      }
      else tree
    }

    private def isExternalObject(tpt: Tree, data: Data): Boolean =
      try {
        val typed = getType(tpt,true)
        // TODO: do we still need the check for tCRef (or can we only check for tCObjWrapper)
        typed.baseClasses.map(_.asType.toType).exists( t => t <:< tCObjObject) ||
          this.findAnnotation(typed.typeSymbol,"scalanative.cobj.CObj.CObjWrapper").isDefined ||
          data.currentType == getQualifiedTypeName(tpt,true)
      } catch {
        case ex: TypecheckException => false
      }

    private def isExtern(rhs: Tree): Boolean = rhs match {
      case Ident(TermName(id)) =>
        id == "extern"
      case Select(_,name) =>
        name.toString == "extern"
      case x =>
        false
    }

    private def isAbstract(t: TypeTransformData[TypeParts]): Boolean = t match {
      case cls : ClassTransformData => t.modParts.modifiers.hasFlag(Flag.ABSTRACT)
      case _ => false
    }

    private def nullable(m: DefDef): Boolean =
      findAnnotation(m.mods.annotations,"scala.scalanative.cobj.nullable").isDefined
  }
}
