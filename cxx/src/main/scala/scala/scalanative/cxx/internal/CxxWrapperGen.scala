package scala.scalanative.cxx.internal

import scala.annotation.StaticAnnotation
import scala.reflect.macros.whitebox
import scala.scalanative.cobj.NamingConvention
import scala.scalanative.cobj.internal.CommonHandler
import scala.scalanative.cxx.{CxxEnum, CxxObject}
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
  private val tPtrInt = weakTypeOf[Ptr[Int]]
  private val tPtrLong = weakTypeOf[Ptr[Long]]
  private val tPtrDouble = weakTypeOf[Ptr[Double]]
  private val tPtrFloat = weakTypeOf[Ptr[Float]]
  private val tPtr = weakTypeOf[Ptr[_]]
  private val tRawPtr = weakTypeOf[RawPtr]
  protected val tCxxObject = weakTypeOf[CxxObject]

  sealed trait CxxType {
    def name: String
    def default: String
    def ptr: String = name + "*"
    def ref: String = name + "&"
  }
  sealed trait PrimitiveType extends CxxType { def default = name }
  case object BoolType       extends PrimitiveType { val name = "bool" }
  case object CharType       extends PrimitiveType { val name = "char" }
  case object IntType        extends PrimitiveType { val name = "int" }
  case object LongType       extends PrimitiveType { val name = "long" }
  case object FloatType      extends PrimitiveType { val name = "float" }
  case object DoubleType     extends PrimitiveType { val name = "double" }
  case object UnitType       extends PrimitiveType { val name = "void" }
  case object CStringType    extends PrimitiveType { val name = "char*" }
  case object CStringPtrType extends PrimitiveType { val name = "char**" }
  case object IntPtrType     extends PrimitiveType { val name = "int*" }
  case object LongPtrType    extends PrimitiveType { val name = "long*" }
  case object FloatPtrType   extends PrimitiveType { val name = "float*" }
  case object DoublePtrType  extends PrimitiveType { val name = "double*" }
  case object VoidPtr        extends PrimitiveType { val name = "void*" }
  case class EnumType(name: String) extends CxxType { def default = "int" }
  case class ClassType(name: String) extends CxxType { def default = ptr }

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

  protected def genCxxWrapper(src: String): Tree = q"""new scalanative.annotation.InlineSource("Cxx",${Literal(Constant(src))})"""

  protected def genCxxWrapperAnnot(data: CxxMacroData): Tree = {
    val cxxName = Literal(Constant(data.cxxFQClassName))
    q"new scalanative.cxx.internal.CxxWrapper($cxxName)"
  }

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
    val returnType = genCxxReturnType(scalaDef,returnsConst(scalaDef))
    val cast = if(returnsRef(scalaDef)) s"($returnType)&" else ""
    val name = genCxxName(scalaDef)
    val scalaName = genScalaName(scalaDef)
    val (params, callArgs) = genCxxParams(scalaDef)
    val clsPtr = data.cxxFQClassName + "* p"
    val ret = returnType match {
      case "void" => ""
      case _ => "return " + cast
    }
    s"""$returnType ${data.externalPrefix}$scalaName(${(clsPtr+:params).mkString(", ")}) { $ret p->$name(${callArgs.mkString(", ")}); }"""
  }

  protected def genCxxFunctionWrapper(scalaDef: DefDef)(implicit data: Data): String = {
    val returnType = genCxxReturnType(scalaDef,returnsConst(scalaDef))
    val cast = if(returnsRef(scalaDef)) s"($returnType)&" else ""
    val name = genCxxName(scalaDef)
    val scalaName = genScalaName(scalaDef)
    val (params, callArgs) = genCxxParams(scalaDef)
    val ret = returnType match {
      case "void" => ""
      case _ => "return " + cast
    }
    s"""$returnType ${data.externalPrefix}$scalaName(${params.mkString(", ")}) { $ret ${data.cxxFQClassName}::$name(${callArgs.mkString(", ")}); }"""
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

  protected def genCxxReturnType(scalaDef: DefDef, returnsConst: Boolean)(implicit data: Data): String = {
    val cxxType = genCxxWrapperType(getType(scalaDef.tpt,true)).default
    if(returnsConst)
      "const "+cxxType
    else
      cxxType
  }

  protected def genCxxName(scalaDef: DefDef)(implicit data: Data): String =
    findAnnotation(scalaDef.mods.annotations,"scala.scalanative.cxx.cxxName") match {
      case Some(annot) => extractAnnotationParameters(annot,Seq("name")).apply("name") match {
        case Some(name) =>
          extractStringConstant(name).getOrElse(scalaDef.name.toString)
        case _ => ???
      }
      case _ => scalaDef.name.toString
    }


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
    val cast = castParam(cxxType,isRef(param))
    (s"${cxxType.default} $name",s"$cast$name")
  }

  private def castParam(cxxType: CxxType, isRef: Boolean): String = cxxType match {
      case en: EnumType =>
        s"(${en.name + (if(isRef) "&" else "")})"
      case x if isRef =>
        "*"
//        s"(${x.name}&)"
      case _ => ""
    }

  protected def genCxxWrapperType(tpe: Tree, isConst: Boolean)(implicit data: Data): CxxType =
    genCxxWrapperType(getType(tpe, true))

  protected def genCxxWrapperType(tpe: Type)(implicit data: Data): CxxType = tpe match {
    case t if t =:= tBoolean    => BoolType
    case t if t =:= tChar       => CharType
    case t if t =:= tInt        => IntType
    case t if t =:= tLong       => LongType
    case t if t =:= tFloat      => FloatType
    case t if t =:= tDouble     => DoubleType
    case t if t =:= tUnit       => UnitType
    case t if t =:= tCString    => CStringType
    case t if t =:= tPtrCString => CStringPtrType
    case t if t =:= tPtrInt     => IntPtrType
    case t if t =:= tPtrLong    => LongPtrType
    case t if t =:= tPtrFloat   => FloatPtrType
    case t if t =:= tPtrDouble  => DoublePtrType
    case t if t =:= tRawPtr     => VoidPtr
    case t if t <:< tPtr        => VoidPtr
    case t if t <:< tCxxObject  => ClassType(genCxxExternalType(t))
    case t if t <:< tCEnum    => EnumType(genCxxEnumType(t))
//    case t if t <:< tCObject => "void*"
    case t => ClassType(genCxxExternalType(t))
  }

  private def genCxxEnumType(t: Type): String =
    extractAnnotationParameters(t.typeSymbol,"scala.scalanative.unsafe.name",Seq("name")).get.apply("name") match {
      case Some(name) => extractStringConstant(name).get
      case None =>
        t.resultType.toString.split("\\.").dropRight(1).last
    }

  private def genCxxExternalType(tpe: Type)(implicit data: Data): String = {
    if(tpe.typeSymbol.fullName == data.currentType)
      data.cxxFQClassName
    else
      extractAnnotationParameters(tpe.typeSymbol,"scala.scalanative.cxx.internal.CxxWrapper",Seq("cxxType")) match {
        case Some(args) => extractStringConstant(args("cxxType").get).get
        case _ =>
          genCxxFQClassName(tpe)
      }
  }

  protected def genCxxFQClassName(tpe: CommonParts)(data: Data): String =
    (data.cxxNamespace,data.cxxClassName) match {
      case (None,None) => tpe.nameString
      case (Some(ns),None) => ns + "::" + tpe.nameString
      case (None,Some(cn)) => cn
      case (Some(ns),Some(cn)) => ns + "::" + cn
    }

  protected def genCxxFQClassName(tpe: Type)(implicit data: Data): String =
    tpe.typeSymbol.fullName.replaceAll("\\.","::")

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

  protected def isRef(p: ValDef): Boolean =
    findAnnotation(p.mods.annotations,"scala.scalanative.cxx.ref").isDefined
}

