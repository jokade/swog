package scala.scalanative.cxx

import de.surfice.smacrotools.MacroAnnotationHandler

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.reflect.macros.whitebox
import scala.language.experimental.macros
import scala.scalanative.cobj.internal.CommonHandler
import scala.scalanative.cxx.internal.CxxWrapperGen
import scala.scalanative.runtime.RawPtr
import scala.scalanative.unsafe.extern

@compileTimeOnly("enable macro paradise to expand macro annotations")
class ScalaCxx(namespace: String = null, classname: String = null, prefix: String = null) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ScalaCxx.Macro.impl
}

object ScalaCxx {
  private[cxx] class Macro(val c: whitebox.Context) extends CommonHandler with CxxWrapperGen {
    override def annotationName = "scala.scalanative.native.cxx.ScalaCxx"
    override def supportsClasses: Boolean = true
    override def supportsTraits: Boolean = false
    override def supportsObjects: Boolean = false
    override def createCompanion: Boolean = true

    val annotationParamNames = Seq("namespace","classname","prefix")

    import c.universe._

    private val registerFunc = q"""@scalanative.cxx.cxxBody("___register((void**)callbacks);") def ___register(callbacks: scalanative.unsafe.Ptr[scalanative.runtime.RawPtr]): Unit = scalanative.unsafe.extern"""

    private val setWrapperFunc = q"""def ___setWrapper(w: RawPtr): Unit = scalanative.unsafe.extern"""

    private val setWrapperStmt = q"""___setWrapper(scalanative.runtime.Intrinsics.castObjectToRawPtr(this))"""

    case class CxxMember(instanceMember: String, callbackDecl: String, callbackDefn: String, callbackReg: String, term: TermName)

    implicit class ScalaCxxData(data: Map[String,Any]) extends CxxMacroData(data) {

      def scalaCxxInstancePublicMembers: Seq[CxxMember] = data.getOrElse("scalaCxxInstancePublicMembers",Nil).asInstanceOf[Seq[CxxMember]]
      def withScalaCxxInstancePublicMembers(members: Seq[CxxMember]): Data = data.updated("scalaCxxInstancePublicMembers",members)
//      def cxxName: String = data.getOrElse("scalaCxxName","").asInstanceOf[String]
//      def withCxxName(name: String): Data = data.updated("scalaCxxName",name)
    }

    override def analyze: Analysis = super.analyze andThen {
      case (cls: ClassParts, data) =>
        val companion = cls.companion.get
        val updCls = cls.copy(
          companion = Some(companion.copy(body = companion.body :+ registerFunc)),
          body = cls.body :+ setWrapperFunc)
        val updData = (
          analyzeMainAnnotation(updCls) _
            andThen analyzeTypes(updCls) _
            andThen analyzeConstructor(updCls) _
            andThen analyzeBody(updCls) _
          )(data)
        (updCls,updData)
      case (obj: ObjectParts, data) =>
        val updObj = obj.copy(body = obj.body :+ registerFunc)
        val updData = (
          analyzeMainAnnotation(updObj) _
            andThen analyzeBody(updObj) _
          )(data)
        (updObj,updData)
      case default => default
    }


    override def transform: Transformation = super.transform andThen {
      case cls: ClassTransformData =>
        val updCls =
          cls
            .addStatements(setWrapperFunc)
            .addStatements(setWrapperStmt)
        updCls
          .updBody(genTransformedTypeBody(updCls))
          .addAnnotations(genCxxWrapperAnnot(updCls.data))
          .updCtorParams(genTransformedCtorParams(updCls))
          .updParents(genTransformedParents(updCls))
      case obj: ObjectTransformData =>
        val updObj = obj.updBody(obj.modParts.body:+registerFunc)
        val externalSource = genCxxWrapper(genScalaCxxClass(obj.data))
        val transformedBody = genTransformedCompanionBody(updObj) ++ obj.data.additionalCompanionStmts :+ genBindingsObject(obj.data) :+ genRegistration(obj.data)
        obj
          .updBody(transformedBody)
          .addAnnotations(externalSource,genCxxSource(obj.data))
      case default => default
    }

    private def analyzeMainAnnotation(tpe: CommonParts)(data: Data): Data = {
      val annotParams = extractAnnotationParameters(c.prefix.tree, annotationParamNames)
      val externalPrefix = annotParams("prefix") match {
        case Some(prefix) => extractStringConstant(prefix).get
        case None => genPrefixName(tpe)
      }
      val namespace = annotParams("namespace") match {
        case Some(ns) => extractStringConstant(ns).get.trim match {
          case "" => None
          case x => Some(x)
        }
        case _ =>
          Some(tpe.fullName.split("\\.").init.mkString("::"))
      }
      val classname = annotParams("classname") match {
        case Some(cn) => extractStringConstant(cn).get.trim match {
          case "" => None
          case x => Some(x)
        }
        case _ => None
      }
      val updData = data
        .withExternalPrefix(externalPrefix)
        .withCxxNamespace(namespace)
        .withCxxClassName(classname)
      analyzeCxxAnnotation(tpe)(updData)
    }

    override def analyzeBody(tpe: CommonParts)(data: Data): Data =
      ( super.analyzeBody(tpe) _ andThen analyzeScalaCxxBody(tpe) )(data)

    private def analyzeScalaCxxBody(tpe: CommonParts)(data: Data): Data = tpe match {
      case t: TypeParts =>
        val updData = genScalaCxxMethodCallbacks(t)(data)
//        if(t.companion.isDefined)
//          genCxxObjectWrappers(t.companion.get)(updData)
//        else
          updData
      case o: ObjectParts =>
        data
        //genCxxObjectWrappers(o)(data)
    }

    private def genScalaCxxMethodCallbacks(tpe: TypeParts)(implicit data: Data): Data = {
      val (callbacks,wrappers) = tpe.body.collect {
        case t: DefDef if (isPublic(t) && !isExtern(t.rhs)) =>
          (genScalaCxxCallback(t,tpe.name),genScalaCxxMethodWrapper(t))
      }.unzip
      val callbacksObject =
        q"""object __callbacks {
              import scala.scalanative.runtime.Intrinsics
              ..$callbacks
            }"""
      data
        .withAdditionalCompanionStmts(Seq(callbacksObject))
        .withScalaCxxInstancePublicMembers(wrappers)
    }

    private def genScalaCxxCallback(scalaDef: DefDef, classType: TypeName)(implicit data: Data) = {

      val (scalaCallbackParams,args,callbackTypes) = scalaDef.vparamss match {
        case Nil => (Nil,Nil,Nil)
        case List(args) => (
          transformExternalBindingParams(args,data),
          transformExternalCallArgs(args,data),
        args.map(tranformScalaCallbackType))
      }
//      val call = q"42"
      val call =
        if(scalaDef.vparamss.isEmpty)
          q"o.${scalaDef.name}"
        else
          q"o.${scalaDef.name}(..$args)"

      val instance = q"val o = Intrinsics.castRawPtrToObject(ptr).asInstanceOf[$classType]"
//      val instance = q""

      val scalaCallbackReturnType = tranformScalaCallbackType(scalaDef)

      val callback = scalaCallbackParams.size match {
        case 0 =>
          q"""new CFuncPtr1[$tpeRawPtr,$scalaCallbackReturnType] {
                def apply(ptr: $tpeRawPtr): $scalaCallbackReturnType = {
                  $instance
                  $call
                }
              }"""
        case 1 =>
          q"""new CFuncPtr2[$tpeRawPtr,..$callbackTypes,$scalaCallbackReturnType] {
                def apply(ptr: $tpeRawPtr, ..$scalaCallbackParams): $scalaCallbackReturnType = {
                  $instance
                  $call
                }
              }"""
      }
      val defn = q"val ${scalaDef.name} = $callback"
      defn
    }

    private def tranformScalaCallbackType(scalaDef: ValOrDefDef)(implicit data: Data) = getType(scalaDef.tpt,true) match {
      case t if t <:< tCObject => tpePtrByte
      case _ => scalaDef.tpt
    }

    private def genPrefixName(tpe: CommonParts): String =
      tpe.fullName.replaceAll("\\.","_") + "_"


    private def genScalaCxxMethodWrapper(scalaDef: DefDef)(implicit data: Data): CxxMember = {
      val fqClassname = data.cxxFQClassName
      val name = genCxxName(scalaDef)
      val rettype = genCxxReturnType(scalaDef,false)
      val (params,args) = genCxxParams(scalaDef)
      val paramString = params.mkString(", ")
      val argString = ("this->___wrapper" +: args).mkString(", ")
      val method = s"""$rettype $name($paramString) { return __$name($argString); }"""
      val callbackParams = ("void* __p" +: params).mkString(", ")
      val callbackDecl = s"""static $rettype (*__$name)($callbackParams);"""
      val callbackDefn = s"""$rettype (*$fqClassname::__$name)($callbackParams);"""
      val callbackReg = s"$fqClassname::__$name = ($rettype (*)($callbackParams))"
      CxxMember(method,callbackDecl,callbackDefn,callbackReg,TermName(name))
    }

    private def genScalaCxxClass(implicit data: ScalaCxxData): String = {
      val namespace = genNamespace
      val classname = genClassname
      val instanceMembers = data.scalaCxxInstancePublicMembers.map(_.instanceMember)
      val callbackDecls = data.scalaCxxInstancePublicMembers.map(_.callbackDecl)
      val callbackDefns = data.scalaCxxInstancePublicMembers.map(_.callbackDefn)
      val callbackRegs = data.scalaCxxInstancePublicMembers.map(_.callbackReg).zipWithIndex.map{ p =>
        s"    ${p._1}callbacks[${p._2}];"
      }

      val register =
        s"""static void ___register(void* callbacks[]) {
           |${callbackRegs.mkString("\n")}
           |  }""".stripMargin

      val classdef =
        s"""class $classname {
           |  void* ___wrapper;
           |public:
           |  void ___setWrapper(void* w) { this->___wrapper = w; }
           |  $register
           |  ${callbackDecls.mkString("\n  ")}
           |           |  ${instanceMembers.mkString("\n  ")}
           |};""".stripMargin

      if(namespace.isDefined)
        s"""namespace ${namespace.get} {
           |$classdef
           |}
           |${callbackDefns.mkString("\n")}
           |$register
           |""".stripMargin
      else
        classdef
    }

    private def genRegistration(implicit data: ScalaCxxData): Tree = {
      val callbacks = data.scalaCxxInstancePublicMembers
      val size = Literal(Constant(callbacks.size))
      val assignments = callbacks
        .map(_.term)
        .zipWithIndex
        .map{ p =>
          val i = Literal(Constant(p._2))
          q"array($i) = Boxes.unboxToCFuncRawPtr(__callbacks.${p._1})"
        }
      q"""{
            import scalanative.unsafe._
            import scalanative.runtime._
            val array = stackalloc[RawPtr]($size)
              ..$assignments
            ___register(array)
          }"""
    }

    private def genNamespace(implicit data: CxxMacroData): Option[String] = {
      val ns = data.cxxFQClassName.split("::").init
      if(ns.isEmpty)
        None
      else
        Some(ns.mkString("::"))
    }

    private def genClassname(implicit data: CxxMacroData): String =
      data.cxxFQClassName.split("::").last

    private def isPublic(scalaDef: ValOrDefDef): Boolean =
      !(scalaDef.mods.hasFlag(Flag.PRIVATE) || scalaDef.mods.hasFlag(Flag.PROTECTED))
  }
}
