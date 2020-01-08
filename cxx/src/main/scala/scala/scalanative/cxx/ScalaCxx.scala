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

    private def registerFunc(data: Data) = {
      val cxxBody = Literal(Constant(data.cxxFQClassName+"::___register((void**)callbacks);"))
      q"""@scalanative.cxx.cxxBody($cxxBody) def ___register(callbacks: scalanative.unsafe.Ptr[$tpeRawPtr]): Unit = scalanative.unsafe.extern"""
    }

    //    private val registerFunc = q"""@scalanative.cxx.cxxBody("___register((void**)callbacks);") def ___register(callbacks: scalanative.unsafe.Ptr[$tpeRawPtr]): Unit = scalanative.unsafe.extern"""

    private val setWrapperFunc = q"""override def ___setWrapper(w: $tpeRawPtr): Unit = scalanative.unsafe.extern"""

    private val setWrapperStmt = q"""___setWrapper(scalanative.runtime.Intrinsics.castObjectToRawPtr(this))"""

    private val tScalaCxxObject = weakTypeOf[ScalaCxxObject]
    private val tpeScalaCxxObject = tq"$tScalaCxxObject"
    override protected def tpeDefaultParent = tpeScalaCxxObject

    case class CxxMember(instanceMember: String, callbackDecl: String, callbackDefn: String, callbackReg: String, term: TermName)

    implicit class ScalaCxxData(data: Map[String,Any]) extends CxxMacroData(data) {

      def scalaCxxInstancePublicMembers: Seq[CxxMember] = data.getOrElse("scalaCxxInstancePublicMembers",Nil).asInstanceOf[Seq[CxxMember]]
      def withScalaCxxInstancePublicMembers(members: Seq[CxxMember]): Data = data.updated("scalaCxxInstancePublicMembers",members)

      def scalaCxxParentClass: Option[String] = data.getOrElse("scalaCxxParentClass",None).asInstanceOf[Option[String]]
      def withScalaCxxParentClass(parent: Option[String]): Data = data.updated("scalaCxxParentClass",parent)
//      def cxxName: String = data.getOrElse("scalaCxxName","").asInstanceOf[String]
//      def withCxxName(name: String): Data = data.updated("scalaCxxName",name)
    }

    override def analyze: Analysis = super.analyze andThen {
      case (cls: ClassParts, data) =>
        val updCls = cls.copy(body = cls.body :+ setWrapperFunc)
        val updData = (
          analyzeMainAnnotation(updCls) _
            andThen analyzeTypes(updCls)
            andThen analyzeConstructor(updCls)
            andThen analyzeBody(updCls)
            andThen addScalaCxxWrapperFunctions(updCls)
          )(data)

        (updCls,updData)
//      case (obj: ObjectParts, data) =>
//        val updObj = obj.copy(body = obj.body :+ registerFunc)
//        val updData = (
//          analyzeMainAnnotation(updObj) _
//            andThen analyzeBody(updObj)
//          )(data)
//        (updObj,updData)
      case default => default
    }


    override def transform: Transformation = super.transform andThen {
      case cls: ClassTransformData =>
        val updCls =
          cls
            .addStatements(setWrapperFunc)
        transformClass(updCls)
      case obj: ObjectTransformData =>
        val updObj = obj.updBody(obj.modParts.body:+registerFunc(obj.data))
//        transformObject(updObj)
        val externalSource = genCxxWrapper(genScalaCxxClass(obj.data))
        val transformedBody = genTransformedCompanionBody(updObj) ++ obj.data.additionalCompanionStmts :+ genBindingsObject(obj.data) :+ genRegistration(obj.data)
        updObj
          .updBody(transformedBody)
          .addAnnotations(externalSource,genCxxSource(obj.data, isObject = true, isTrait = false))
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
      ( super.analyzeBody(tpe) _
        andThen genRegisterExternal(tpe)
        andThen analyzeScalaCxxBody(tpe) )(data)

    override def analyzeTypes(tpe: TypeParts)(data: Data): Data =
      (super.analyzeTypes(tpe) _ andThen genScalaCxxParentClass(tpe))(data)

    private def genRegisterExternal(tpe: CommonParts)(data: Data): Data =
      if(tpe.isObject)
        data
      else {
        val prefix = data.externalPrefix
        val registerExternal = genExternalBinding(prefix,registerFunc(data).asInstanceOf[DefDef],false)(data)
        data.addExternals(Seq(registerExternal))
      }

    private def genScalaCxxParentClass(tpe: TypeParts)(data: Data): Data = {
      val parent = tpe.parents.headOption.map(p => getType(p,true)) match {
        case Some(t) if t =:= tAnyRef || t =:= tCxxObject => None
        case Some(t) => findAnnotation(t.typeSymbol,"scala.scalanative.cxx.internal.CxxWrapper").flatMap { annot =>
          extractAnnotationParameters(annot,Seq("cxxType")).apply("cxxType").map(extractStringConstant).get
        }
        case _ => None
      }
      data
        .withScalaCxxParentClass(parent)
    }

    private def analyzeScalaCxxBody(tpe: CommonParts)(data: Data): Data = tpe match {
      case t: TypeParts =>
        val updData =
          (genCxxTypeWrappers(t) _
            andThen genScalaCxxMethodCallbacks(t)) (data)
        if(t.companion.isDefined)
          genCxxObjectWrappers(t.companion.get)(updData)
        else
          updData
      case o: ObjectParts =>
        genCxxObjectWrappers(o)(data)
    }

    private def addScalaCxxWrapperFunctions(tpe: CommonParts)(data: Data): Data =
      data
        .addCxxFunctionWrappers(Seq(registerFunc(data)) map {
          case scalaDef: DefDef => genCxxFunctionWrapper(scalaDef)(data)
        })

    private def genScalaCxxMethodCallbacks(tpe: TypeParts)(data: Data): Data = {
      implicit val d = data
      val (callbacks,wrappers) = tpe.body.collect {
        case t: DefDef if (isPublic(t) && !isExtern(t.rhs)) =>
          (genScalaCxxCallback(t,tpe.name),genScalaCxxMethodWrapper(t,tpe))
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
          transformCallbackCallArgs(args,data),
        args.map(tranformScalaCallbackType))
      }

      val resultType = getType(scalaDef.tpt,true)
      val call = (scalaDef.vparamss.isEmpty, resultType <:< tCObject) match {
        case (true,true) => q"o.${scalaDef.name}.__ptr"
        case (true,false) => q"o.${scalaDef.name}"
        case (false,true) => q"o.${scalaDef.name}(..$args).__ptr"
        case (false,false) => q"o.${scalaDef.name}(..$args)"
      }

      val instance = q"val o = Intrinsics.castRawPtrToObject(ptr).asInstanceOf[$classType]"

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
        case 2 =>
          q"""new CFuncPtr3[$tpeRawPtr,..$callbackTypes,$scalaCallbackReturnType] {
                def apply(ptr: $tpeRawPtr, ..$scalaCallbackParams): $scalaCallbackReturnType = {
                  $instance
                  $call
                }
              }"""
        case 3 =>
          q"""new CFuncPtr4[$tpeRawPtr,..$callbackTypes,$scalaCallbackReturnType] {
                def apply(ptr: $tpeRawPtr, ..$scalaCallbackParams): $scalaCallbackReturnType = {
                  $instance
                  $call
                }
              }"""
        case 4 =>
          q"""new CFuncPtr5[$tpeRawPtr,..$callbackTypes,$scalaCallbackReturnType] {
                def apply(ptr: $tpeRawPtr, ..$scalaCallbackParams): $scalaCallbackReturnType = {
                  $instance
                  $call
                }
              }"""
        case 5 =>
          q"""new CFuncPtr6[$tpeRawPtr,..$callbackTypes,$scalaCallbackReturnType] {
                def apply(ptr: $tpeRawPtr, ..$scalaCallbackParams): $scalaCallbackReturnType = {
                  $instance
                  $call
                }
              }"""
      }
      val defn = q"val ${scalaDef.name} = $callback"
      defn
    }

    private def transformCallbackCallArgs(args: Seq[ValDef], data: Data, outArgs: Boolean = false, wrappers: List[ValDef] = Nil): Seq[Tree] = {
      args filter(implicitParamsFilter) map {
        case ValDef(_,name,tpt,_) if isExternalObject(tpt,data) || outArgs =>
          findWrapper(tpt,wrappers) match {
            case Some(wrapperName) => q"""$wrapperName.wrap($name)"""
            case _ => q"""if($name==null) null else new $tpt($name)"""
          }
        case ValDef(_,name,tpt,_) if isCEnum(tpt,data) =>
          q"new $tpt($name)"
        case ValDef(_,name,AppliedTypeTree(tpe,_),_) if tpe.toString == "_root_.scala.<repeated>" => q"$name:_*"
        case ValDef(_,name,tpt,_) => q"$name"
      }
    }

    private def tranformScalaCallbackType(scalaDef: ValOrDefDef)(implicit data: Data) = getType(scalaDef.tpt,true) match {
      case t if t <:< tCObject => tpePtrByte
      case t if t <:< tCEnum => tpeInt
      case _ => scalaDef.tpt
    }

    private def genPrefixName(tpe: CommonParts): String =
      tpe.fullName.replaceAll("\\.","_") + "_"


    private def genScalaCxxMethodWrapper(scalaDef: DefDef, tpe: TypeParts)(implicit data: Data): CxxMember = {
      val fqClassname = data.cxxFQClassName
      val name = genCxxName(scalaDef)
      val rettype = genCxxReturnType(scalaDef,false)
      val (params,args) = genCxxParams(scalaDef)
      val method =  genCxxImpl(scalaDef,tpe)
      val callbackParams = ("void* __p" +: params).mkString(", ")
      val callbackDecl = s"""static $rettype (*__$name)($callbackParams);"""
      val callbackDefn = s"""$rettype (*$fqClassname::__$name)($callbackParams);"""
      val callbackReg = s"$fqClassname::__$name = ($rettype (*)($callbackParams))"
      CxxMember(method,callbackDecl,callbackDefn,callbackReg,TermName(name))
    }


    private def genCxxImpl(scalaDef: DefDef, tpe: TypeParts)(implicit data: Data): String = {
      val implAnnotation =
        // check if the current method itself is annotated with @cxxSignature
        findAnnotation(scalaDef.mods.annotations, "scala.scalanative.cxx.cxxImpl")
          // else check if one of its parent is annotated
          .orElse (
            tpe.parents
              .flatMap(p => getType(p, true).members)
              .find(_.name == scalaDef.name)
              .flatMap(sym => findAnnotation(sym.asMethod, "scala.scalanative.cxx.cxxImpl"))
          )
      implAnnotation
        .flatMap(annot => extractAnnotationParameters(annot,Seq("impl")).apply("impl").map(extractStringConstant).get)
        .getOrElse {
          val (params,args) = genCxxParams(scalaDef)
          val name = genCxxName(scalaDef)
          val rettype = genCxxReturnType(scalaDef,false)
          val paramString = params.mkString(", ")
          val argString = ("this->___wrapper" +: args).mkString(", ")
          s"""$rettype $name($paramString) { return __$name($argString); }"""
        }
    }

    private def genScalaCxxClass(implicit data: ScalaCxxData): String = {
      val namespace = genNamespace
      val classname = genClassname
      val parentClass = data.scalaCxxParentClass.map(cls => " : "+cls).getOrElse("")
      val includes = data.cxxIncludes.map(i => s"#include $i").mkString("\n")
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
        s"""class $classname $parentClass {
           |  void* ___wrapper;
           |public:
           |  void ___setWrapper(void* w) { this->___wrapper = w; }
           |  $register
           |  ${callbackDecls.mkString("\n  ")}
           |           |  ${instanceMembers.mkString("\n  ")}
           |};""".stripMargin

      val ns =
        if(namespace.isDefined)
          s"""namespace ${namespace.get} {
             |$classdef
             |}
             |""".stripMargin
        else
          classdef

      s"""$includes
         |$ns
         |${callbackDefns.mkString("\n")}
         |$register
         |""".stripMargin
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
