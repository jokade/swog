package scala.scalanative.cxx.internal

import scala.reflect.macros.whitebox
import scala.scalanative.cobj.NamingConvention
import scala.scalanative.cobj.internal.CommonHandler
import scala.scalanative.cxx.CxxObject
import scala.scalanative.runtime.RawPtr
import scala.scalanative.unsafe.{CChar, CDouble, CFloat, CInt, CLong, CString, Ptr}

trait CxxWrapperGen extends CommonHandler {
  val c: whitebox.Context
  import c.universe._

  def annotationParamNames: Seq[String]

  private val tUnit = weakTypeOf[Unit]
  private val tBoolean = weakTypeOf[Boolean]
  private val tChar = weakTypeOf[CChar]
  private val tInt = weakTypeOf[CInt]
  private val tLong = weakTypeOf[CLong]
  private val tFloat = weakTypeOf[CFloat]
  private val tDouble = weakTypeOf[CDouble]
  private val tCString = weakTypeOf[CString]
  private val tPtrCString = weakTypeOf[Ptr[CString]]
  private val tPtr = weakTypeOf[Ptr[_]]
  private val tRawPtr = weakTypeOf[RawPtr]
  protected val tCxxObject = weakTypeOf[CxxObject]

  implicit class CxxMacroData(data: Map[String,Any]) {
    type Data = Map[String, Any]
    type CxxWrapper = String

    def cxxWrappers: Seq[CxxWrapper] = data.getOrElse("cxxWrappers", Nil).asInstanceOf[Seq[CxxWrapper]]
    def withCxxWrappers(wrappers: Seq[CxxWrapper]): Data = data.updated("cxxWrappers",wrappers)
    def addCxxWrappers(wrappers: Seq[CxxWrapper]): Data = data.updated("cxxWrappers",cxxWrappers ++ wrappers)

    def cxxNamespace: Option[String] = data.getOrElse("cxxNamespace",None).asInstanceOf[Option[String]]
    def withCxxNamespace(namespace: Option[String]): Data = data.updated("cxxNamespace",namespace)

    def cxxClassName: Option[String] = data.getOrElse("cxxClassName",None).asInstanceOf[Option[String]]
    def withCxxClassName(className: Option[String]) = data.updated("cxxClassName",className)

    def cxxFQClassName: String = data.getOrElse("cxxFQClassName",null).asInstanceOf[String]
    def withCxxFQClassName(className: String): Data = data.updated("cxxFQClassName",className)

    def cxxIncludes: Seq[String] = data.getOrElse("cxxIncludes",Nil).asInstanceOf[Seq[String]]
    def withCxxIncludes(headers: Seq[String]): Data = data.updated("cxxIncludes",headers)
  }


  def analyzeCxxAnnotation(tpe: CommonParts)(data: Data): Data = {
    val includes = findAnnotations(tpe.modifiers.annotations)
      .collect{
        case ("include",annot) => extractAnnotationParameters(annot,Seq("header"))
      }
      .map( _.apply("header").get )
      .flatMap(extractStringConstant)

    data
      .withNamingConvention(NamingConvention.CxxWrapper)
      .withCxxIncludes(includes)
      .withCxxFQClassName(genCxxFQClassName(tpe)(data))
  }

  def analyzeCxxBody(tpe: CommonParts)(data: Data): Data = tpe match {
    case t: TypeParts =>
      val updData = genCxxTypeWrappers(t)(data)
      if(t.companion.isDefined)
        genCxxObjectWrappers(t.companion.get)(updData)
      else
        updData
    case o: ObjectParts => genCxxObjectWrappers(o)(data)
  }

  def genCxxSource(data: CxxMacroData): Tree = {
    val includes = data.cxxIncludes.map( i => "#include "+i ).mkString("","\n","\n\n")
    genCxxWrapper( includes + """extern "C" {""" + "\n" + data.cxxWrappers.mkString("\n") + "\n}" )
  }

  protected def genCxxWrapper(src: String): Tree = q"""new scalanative.annotation.ExternalSource("Cxx",${Literal(Constant(src))})"""

  protected def genCxxTypeWrappers(tpe: TypeParts)(implicit data: Data): Data = {
    val methods = tpe.body.collect {
      case t:DefDef if isDelete(t) =>
        genCxxDeleteWrapper(t)
      case t@DefDef(mods, name, types, args, rettype, rhs) if isExtern(rhs) =>
          genCxxMethodWrapper(t)
    }
    data
      .addCxxWrappers(methods)
  }

  protected def genCxxObjectWrappers(obj: ObjectParts)(implicit data: Data): Data = {
    val functions = obj.body.collect {
      case t@DefDef(mods, name, types, args, rettype, rhs) if isExtern(rhs) => findConstructor(t) match {
        case Some(clsname) =>
          genCxxConstructorWrapper(t,clsname)
        case _ =>
          genCxxFunctionWrapper(t)
      }
    }
    data
      .addCxxWrappers(functions)
  }

  protected def genCxxMethodWrapper(scalaDef: DefDef)(implicit data: Data): String = {
    val returnType = genCxxWrapperType(scalaDef.tpt,returnsConst(scalaDef))
    val cast = if(returnsRef(scalaDef)) s"($returnType)&" else ""
    val name = genCxxName(scalaDef)
    val scalaName = genScalaName(scalaDef)
    val (params, callArgs) = genCxxParams(scalaDef)
    val clsPtr = data.cxxFQClassName + "* p"
    s"""$returnType ${data.externalPrefix}$scalaName(${(clsPtr+:params).mkString(", ")}) { return $cast p->$name(${callArgs.mkString(", ")}); }"""
  }

  protected def genCxxFunctionWrapper(scalaDef: DefDef)(implicit data: Data): String = {
    val returnType = genCxxWrapperType(scalaDef.tpt,returnsConst(scalaDef))
    val cast = if(returnsRef(scalaDef)) s"($returnType)&" else ""
    val name = genCxxName(scalaDef)
    val scalaName = genScalaName(scalaDef)
    val (params, callArgs) = genCxxParams(scalaDef)
    s"""$returnType ${data.externalPrefix}$name(${params.mkString(", ")}) { return $cast ${data.cxxFQClassName}::$name(${callArgs.mkString(", ")}); }"""
  }

  protected def genCxxConstructorWrapper(scalaDef: DefDef, clsname: Option[String])(implicit data: Data): String = {
    val name = genCxxName(scalaDef)
    val scalaName = genScalaName(scalaDef)
    val (params, callArgs) = genCxxParams(scalaDef)
    val constructor = clsname match {
      case Some(name) => name
      case _ => data.cxxFQClassName
    }
    s"""void* ${data.externalPrefix}$scalaName(${params.mkString(", ")}) { return new $constructor(${callArgs.mkString(", ")}); }"""
  }

  protected def genCxxDeleteWrapper(scalaDef: DefDef)(implicit data: Data): String = {
    val scalaName = genScalaName(scalaDef)
    val clsPtr = data.cxxFQClassName + "* p"
    s"""void ${data.externalPrefix}$scalaName($clsPtr) { delete p; }"""
  }

  protected def genCxxName(scalaDef: DefDef)(implicit data: Data): String = scalaDef.name.toString //genScalaName(scalaDef,data.namingConvention)

  protected def genCxxParams(scalaDef: DefDef)(implicit data: Data): (Seq[String],Seq[String]) =
    scalaDef.vparamss match {
      case Nil => (Nil,Nil)
      case List(args) => args.map(genCxxParam).unzip
      case _ =>
        c.error(c.enclosingPosition,"extern methods with multiple parameter lists are not supported for @Cxx classes")
        ???
    }

  protected def genCxxParam(param: ValDef)(implicit data: Data): (String,String) = {
    val name = param.name.toString
    val cxxType = genCxxWrapperType(param.tpt,false)
    (s"$cxxType $name",name)
  }

  protected def genCxxWrapperType(tpe: Tree, isConst: Boolean)(implicit data: Data): String = {
    val cxxtype = genCxxWrapperType(getType(tpe, true))
    if(isConst)
      "const "+cxxtype
    else
      cxxtype
  }

  protected def genCxxWrapperType(tpe: Type)(implicit data: Data): String = tpe match {
    case t if t =:= tBoolean => "bool"
    case t if t =:= tChar => "char"
    case t if t =:= tInt => "int"
    case t if t =:= tLong => "long"
    case t if t =:= tFloat => "float"
    case t if t =:= tDouble => "double"
    case t if t =:= tUnit => "void"
    case t if t =:= tCString => "char*"
    case t if t =:= tPtrCString => "char**"
    case t if t =:= tRawPtr => "void*"
    case t if t <:< tPtr => "void*"
    case t if t <:< tCxxObject => "void*" //genCxxExternalType(t)
//    case t if t <:< tCObject => "void*"
//    case t => genCxxExternalType(t)
  }

  private def genCxxExternalType(tpe: Type)(implicit data: Data): String = {
    val cxxtype =
      if(tpe.typeSymbol.fullName == data.currentType)
        data.cxxFQClassName
      else
        tpe.typeSymbol.fullName
    cxxtype + "*"
  }
  // TODO: this is just a temporary hack that works for the Qt bindings
  // in the generic case we need get the fully qualified C++ type name!
//    tpe.typeSymbol.name.toString + "*"

  protected def genCxxFQClassName(tpe: CommonParts)(data: Data): String =
    (data.cxxNamespace,data.cxxClassName) match {
      case (None,None) => tpe.nameString
      case (Some(ns),None) => ns + "::" + tpe.nameString
      case (None,Some(cn)) => cn
      case (Some(ns),Some(cn)) => ns + "::" + cn
    }

  protected def isConstructor(m: DefDef): Boolean =
    findAnnotation(m.mods.annotations,"scala.scalanative.cxx.constructor").isDefined

  protected def findConstructor(m: DefDef): Option[Option[String]] =
    findAnnotation(m.mods.annotations,"scala.scalanative.cxx.constructor")
    .map(annot => extractAnnotationParameters(annot,Seq("classname")).apply("classname").flatMap(extractStringConstant))
//      .flatMap(annot =>extractAnnotationParameters(annot,Seq("classname")).get("classname").get)
//      .map(extractStringConstant(_))

  protected def isDelete(m: DefDef): Boolean =
    findAnnotation(m.mods.annotations,"scala.scalanative.cxx.delete").isDefined

  protected def returnsConst(m: DefDef): Boolean =
    findAnnotation(m.mods.annotations,"scala.scalanative.cxx.returnsConst").isDefined

  protected def returnsRef(m: DefDef): Boolean =
    findAnnotation(m.mods.annotations,"scala.scalanative.cxx.returnsRef").isDefined
}

