package scala.scalanative.cxx.internal

import scala.annotation.StaticAnnotation
import scala.reflect.macros.whitebox
import scala.scalanative.cobj.{NamingConvention, ResultPtr, ResultValue}
import scala.scalanative.cobj.internal.CommonHandler
import scala.scalanative.cxx.{CxxEnum, CxxObject}
import scala.scalanative.runtime.RawPtr
import scala.scalanative.unsafe.{CChar, CDouble, CFloat, CInt, CLong, CLongLong, CShort, CString, CUnsignedChar, CUnsignedInt, CUnsignedLong, CUnsignedLongLong, CUnsignedShort, Ptr}


trait CxxWrapperGen extends CommonHandler {
  val c: whitebox.Context
  import c.universe._

  def annotationParamNames: Seq[String]

  private val tUnit = weakTypeOf[Unit]
  private val tBoolean = weakTypeOf[Boolean]
  private val tChar = weakTypeOf[CChar]
  private val tShort = weakTypeOf[CShort]
  private val tInt = weakTypeOf[CInt]
  private val tLong = weakTypeOf[CLong]
  private val tLongLong = weakTypeOf[CLongLong]
  private val tFloat = weakTypeOf[CFloat]
  private val tDouble = weakTypeOf[CDouble]
  private val tCString = weakTypeOf[CString]
  private val tUByte = weakTypeOf[CUnsignedChar]
  private val tUShort = weakTypeOf[CUnsignedShort]
  private val tUInt = weakTypeOf[CUnsignedInt]
  private val tULong = weakTypeOf[CUnsignedLong]
  private val tULongLong = weakTypeOf[CUnsignedLongLong]
  private val tPtrCString = weakTypeOf[Ptr[CString]]
  private val tPtrBoolean = weakTypeOf[Ptr[Boolean]]
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
  case object ShortType      extends PrimitiveType { val name = "short" }
  case object IntType        extends PrimitiveType { val name = "int" }
  case object LongType       extends PrimitiveType { val name = "long" }
  case object LongLongType   extends PrimitiveType { val name = "long long" }

  case object UCharType      extends PrimitiveType { val name = "unsigned char" }
  case object UShortType     extends PrimitiveType { val name = "unsigned short" }
  case object UIntType       extends PrimitiveType { val name = "unsigned int" }
  case object ULongType      extends PrimitiveType { val name = "unsigned long" }
  case object ULongLongType  extends PrimitiveType { val name = "unsigned long long" }

  case object FloatType      extends PrimitiveType { val name = "float" }
  case object DoubleType     extends PrimitiveType { val name = "double" }

  case object UnitType       extends PrimitiveType { val name = "void" }
  case object CStringType    extends PrimitiveType { val name = "char*" }
  case object CStringPtrType extends PrimitiveType { val name = "char**" }
  case object IntPtrType     extends PrimitiveType { val name = "int*" }
  case object LongPtrType    extends PrimitiveType { val name = "long*" }
  case object FloatPtrType   extends PrimitiveType { val name = "float*" }
  case object DoublePtrType  extends PrimitiveType { val name = "double*" }
  case object BoolPtrType    extends PrimitiveType { val name = "bool*" }
  case object VoidPtr        extends PrimitiveType { val name = "void*" }
  case class EnumType(name: String) extends CxxType { def default = "int" }
  case class ClassType(name: String) extends CxxType { def default = ptr }

  val notPrivate = Modifiers().privateWithin

  case class Template(instance: TypeParts, templateType: Type, annotation: Tree)(implicit data: Data) {
    val params = extractAnnotationParameters(annotation,Seq("namespace","classname"))

    // If 'namespace' is explicitly set in the @Cxx annotation, use that value;
    // otherwise use the value from the @CxxTemplate annotation
    val namespace = data.cxxNamespace.orElse(extractStringConstant(params("namespace").get))
    // If 'classname' is explicitly set in the @Cxx annotation, use that value;
    // otherwise use the value from the @CxxTemplate annotation
    val classname = data.cxxClassName.orElse(extractStringConstant(params("classname").get))

    // Only add the template type arguments to the FQName if 'classname' was not explicitly set in the @Cxx annotation
    val templateArgs =
      if(data.cxxClassName.isEmpty) templateType.typeArgs.map(genCxxWrapperType(_).default).mkString("<",",",">")
      else ""

    val fqName = ((namespace,classname) match {
      case (None,None) => templateType.typeSymbol.name.toString
      case (Some(ns),None) => ns + "::" + templateType.typeSymbol.name.toString
      case (None,Some(cn)) => cn
      case (Some(ns),Some(cn)) => ns + "::" + cn
    }) + templateArgs

    val includes = analyzeCxxIncludes(templateType.typeSymbol)

    val templateTypes = templateType.baseClasses.head.typeSignature.typeParams
    val instanceTypes = templateType.typeArgs.map(_.typeSymbol)

    def substituteTemplateType(tpe: Type): Type =
      tpe.substituteSymbols(templateTypes,instanceTypes)

    val templateMethods = templateType.decls
      .collect{case f if f.isMethod && f.isAbstract => f}
      .map{f =>
        val cxxBody = findAnnotation(f,"scala.scalanative.cxx.cxxBody")
        val cxxName = findAnnotation(f,"scala.scalanative.cxx.cxxName")
        val mods = Modifiers(NoFlags,notPrivate,cxxBody.toList ++ cxxName.toList)
        val name = f.name.toTermName
        val vparamss =
          f.typeSignature.paramLists.map( l => l.map{p =>
            val ref = findAnnotation(p,"scala.scalanative.cxx.ref")
            val mods = Modifiers(NoFlags,notPrivate,ref.toList)
            val name = p.name.toTermName
            val tpe = substituteTemplateType( p.typeSignature.resultType )
            val tpt = tq"$tpe"
            ValDef(mods,name,tpt,EmptyTree)
          })
        val resultType = substituteTemplateType(f.typeSignature.resultType)
        DefDef(mods,name,Nil,vparamss,tq"$resultType",expExtern)
      }

    val externalBindings = templateMethods.map(f => genExternalBinding(data.externalPrefix,f,true)(data))

    def templateWrappers(data: Data): Seq[String] = templateMethods.map(m => genCxxMethodWrapper(m)(data)).toSeq

    def updData: Data =
      data
        .withCxxTemplate(this)
        .withCxxFQClassName(fqName)
        .withCxxIncludes(includes)
        .addExternals(externalBindings.toMap)
  }

  implicit class CxxMacroData(data: Map[String,Any]) {
    type Data = Map[String, Any]
    type CxxWrapper = String

    // Holds the C++ wrappers for external methods defined in Scala classes
    def cxxMethodWrappers: Seq[CxxWrapper] = data.getOrElse("cxxMethodWrappers", Nil).asInstanceOf[Seq[CxxWrapper]]
    def addCxxMethodWrappers(wrappers: Seq[CxxWrapper]): Data = data.updated("cxxMethodWrappers",cxxMethodWrappers ++ wrappers)

    // Holds the C++ wrappers for external functions defined on Scala objects
    def cxxFunctionWrappers: Seq[CxxWrapper] = data.getOrElse("cxxFunctionWrappers", Nil).asInstanceOf[Seq[CxxWrapper]]
    def addCxxFunctionWrappers(wrappers: Seq[CxxWrapper]): Data = data.updated("cxxFunctionWrappers",cxxFunctionWrappers ++ wrappers)

    def cxxNamespace: Option[String] = data.getOrElse("cxxNamespace",None).asInstanceOf[Option[String]]
    def withCxxNamespace(namespace: Option[String]): Data = data.updated("cxxNamespace",namespace)

    def cxxClassName: Option[String] = data.getOrElse("cxxClassName",None).asInstanceOf[Option[String]]
    def withCxxClassName(className: Option[String]) = data.updated("cxxClassName",className)

    def cxxFQClassName: String = data.getOrElse("cxxFQClassName",null).asInstanceOf[String]
    def withCxxFQClassName(className: String): Data = data.updated("cxxFQClassName",className)

    def cxxIncludes: Seq[String] = data.getOrElse("cxxIncludes",Nil).asInstanceOf[Seq[String]]
    def withCxxIncludes(headers: Seq[String]): Data = data.updated("cxxIncludes",headers)

    def cxxType: String = data.getOrElse("cxxType",cxxFQClassName).asInstanceOf[String]
    def withCxxType(tpe: Option[String]): Data = if(tpe.isDefined) data.updated("cxxType",tpe.get) else data

    /// indicates if the annottee is a object representing functions in a C++ namespace (instead of a class)
    def cxxIsNamespaceObject: Boolean = data.getOrElse("cxxIsNamespaceObject",false).asInstanceOf[Boolean]
    def setCxxIsNamespaceObject(f: Boolean): Data = data.updated("cxxIsNamespaceObject",f)

    /// Returns the template data if this class instantiates a C++ template
    def cxxTemplate: Option[Template] = data.getOrElse("cxxTemplate",None).asInstanceOf[Option[Template]]
    def withCxxTemplate(tpl: Template): Data = data.updated("cxxTemplate",Some(tpl))
  }

  private def genSizeOfExternal(data: Data): Tree = {
    val name = TermName(data.externalPrefix+"__sizeof")
    q"def $name(): Int = $expExtern"
  }

  private def genSizeOfVal(data: Data): Tree = {
    val name = TermName(data.externalPrefix+"__sizeof")
    q"lazy val __sizeof: Int = __ext.$name()"
  }

  private def analyzeCxxIncludes(tpe: Symbol): Seq[String] =
    analyzeCxxIncludes(tpe.annotations.map(_.tree))

  private def analyzeCxxIncludes(annotations: List[Tree]): Seq[String] =
    findAnnotations(annotations)
      .collect{
        case (name,annot)  if name.endsWith("include") => extractAnnotationParameters(annot,Seq("header"))
      }
      .map( _.apply("header").get )
      .flatMap(extractStringConstant)

  def analyzeCxxAnnotation(tpe: CommonParts)(data: Data): Data = {
    val includes = analyzeCxxIncludes(tpe.modifiers.annotations)

    val updData =
      data
      .withNamingConvention(NamingConvention.CxxWrapper)
      .withCxxIncludes(includes)
      .withCxxFQClassName(genCxxFQClassName(tpe)(data))

    // don't add 'sizeof' if the annottee represents a trait or functions in a C++ namespace
    if(tpe.isTrait || data.cxxIsNamespaceObject)
      updData
    else
      updData
      .addExternals(Seq("__sizeof"->("__sizeof"->genSizeOfExternal(data))))
      .withAdditionalCompanionStmts(Seq(genSizeOfVal(data)))
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

  def genCxxSource(data: Data, isTrait: Boolean, isObject: Boolean): Tree = {
    val includes = data.cxxIncludes.map( i => "#include "+i ).mkString("","\n","\n\n")
    val sizeof =
      if(isObject && !data.cxxIsNamespaceObject)
        s"""  int ${data.externalPrefix}__sizeof() { return sizeof(${data.cxxType}); }\n"""
      else
        ""
    val wrappers = if(isObject) data.cxxFunctionWrappers else data.cxxMethodWrappers

    genCxxWrapper( includes + """extern "C" {""" + "\n" + sizeof + wrappers.mkString("\n") + "\n}" )
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
    } ++ data.cxxTemplate.map(_.templateWrappers(data)).getOrElse(Nil)
    data
      .addCxxMethodWrappers(methods)
  }

  protected def genCxxObjectWrappers(obj: ObjectParts)(implicit data: Data): Data = {
    val functions = obj.body.collect {
      case t@DefDef(mods, name, types, args, rettype, rhs) if isExtern(rhs) => findConstructor(t) match {
        case Some(clsname) =>
          val argsNoImplicits = args.head.filter(! _.mods.hasFlag(Flag.IMPLICIT))
          val constr = DefDef(mods,name,types,List(argsNoImplicits),rettype,rhs)
          genCxxConstructorWrapper(constr,clsname)
        case _ =>
          genCxxFunctionWrapper(t)
      }
    }
    data
      .addCxxFunctionWrappers(functions)
  }

  protected def genCxxMethodWrapper(scalaDef: DefDef)(implicit data: Data): String = {
    val returnType = genCxxReturnType(scalaDef,returnsConst(scalaDef))
    val cast = if(returnsRef(scalaDef)) s"($returnType)&" else ""
    val name = genCxxName(scalaDef)
    val scalaName = genScalaName(scalaDef)
    val (params, callArgs) = genCxxParams(scalaDef)
    val clsPtr = data.cxxType + "* __p"
    val ret = returnType match {
      case "void" if returnsValue(scalaDef) => "*__res = "
      case "void" => ""
      case _ => "return " + cast
    }
    val body = findCxxBody(scalaDef).getOrElse(s"$ret __p->$name(${callArgs.mkString(", ")});")
    s"""  $returnType ${data.externalPrefix}$scalaName(${(clsPtr+:params).mkString(", ")}) { $body }"""
  }

  protected def genCxxFunctionWrapper(scalaDef: DefDef)(implicit data: Data): String = {
    val returnType = genCxxReturnType(scalaDef,returnsConst(scalaDef))
    val cast = if(returnsRef(scalaDef)) s"($returnType)&" else ""
    val name = genCxxName(scalaDef)
    val scalaName = genScalaName(scalaDef)
    val (params, callArgs) = genCxxParams(scalaDef)
    val ret = returnType match {
      case "void" if returnsValue(scalaDef) => "*__res = "
      case "void" => ""
      case _ => "return " + cast
    }

    val calleeName =
      if(data.cxxIsNamespaceObject)
        data.cxxNamespace.map(_+"::").getOrElse("") + name
      else
        data.cxxFQClassName + "::" + name

    val body = findCxxBody(scalaDef).getOrElse(s"$ret $calleeName(${callArgs.mkString(", ")});")
    s"""  $returnType ${data.externalPrefix}$scalaName(${params.mkString(", ")}) { $body }"""
  }

  protected def genCxxConstructorWrapper(scalaDef: DefDef, clsname: Option[String])(implicit data: Data): String = {
    val name = genCxxName(scalaDef)
    val scalaName = genScalaName(scalaDef)
    val (params, callArgs) = genCxxParams(scalaDef)
    val constructor = clsname match {
      case Some(name) => name
      case _ => data.cxxFQClassName
    }
    s"""  void* ${data.externalPrefix}$scalaName(${params.mkString(", ")}) { return new $constructor(${callArgs.mkString(", ")}); }"""
  }

  protected def genCxxDeleteWrapper(scalaDef: DefDef)(implicit data: Data): String = {
    val scalaName = genScalaName(scalaDef)
    val clsPtr = data.cxxFQClassName + "* p"
    s"""  void ${data.externalPrefix}$scalaName($clsPtr) { delete p; }"""
  }

  protected def genCxxReturnType(scalaDef: DefDef, returnsConst: Boolean)(implicit data: Data): String = {
    val cxxType =
      try{ genCxxWrapperType(getType(scalaDef.tpt,true)) }
      catch { case _:Throwable => VoidPtr }
//    val cxxType = genCxxWrapperType(getType(scalaDef.tpt,true)).default
//      case rt if returnsValue(scalaDef) => "void"
//      case rt => rt.default
//    }*/
    if(returnsConst)
      "const "+cxxType.default
    else
      cxxType.default
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


  protected def genCxxParams(scalaDef: DefDef)(implicit data: Data): (Seq[String],Seq[String]) = {
    val (params,callArgs) = scalaDef.vparamss match {
      case Nil => (Nil, Nil)
      case List(args) => args.filter(implicitParamsFilter).map(genCxxParam).unzip
      case List(inargs, outargs) => (inargs ++ outargs).filter(implicitParamsFilter).map(genCxxParam).unzip
      case _ =>
        c.error(c.enclosingPosition, "extern methods with multiple parameter lists are not supported for @Cxx classes")
        ???
    }
    (params, callArgs.filter(_.nonEmpty))
  }

  protected def genCxxParam(param: ValDef)(implicit data: Data): (String,String) = {
    val isResultVal = isResultValue(param.tpt)
    val name = if(isResultVal) "__res" else param.name.toString
    val cxxType = genCxxWrapperType(param.tpt,false)
    val cast = castParam(cxxType,isRef(param))
    (s"${cxxType.default} $name",if(isResultVal) "" else s"$cast$name")
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
    try{ genCxxWrapperType(getType(tpe, true)) }
    catch { case _:Throwable => VoidPtr }

  protected def genCxxWrapperType(tpe: Type)(implicit data: Data): CxxType = tpe.dealias match {
    case t if t =:= tBoolean    => BoolType
    case t if t =:= tChar       => CharType
    case t if t =:= tShort      => ShortType
    case t if t =:= tInt        => IntType
    case t if t =:= tLong       => LongType
    case t if t =:= tLongLong   => LongLongType
    case t if t =:= tUByte      => UCharType
    case t if t =:= tUShort     => UShortType
    case t if t =:= tUInt       => UIntType
    case t if t =:= tULong      => ULongType
    case t if t =:= tULongLong  => ULongLongType
    case t if t =:= tFloat      => FloatType
    case t if t =:= tDouble     => DoubleType
    case t if t =:= tUnit       => UnitType
    case t if t =:= tCString    => CStringType
    case t if t =:= tPtrCString => CStringPtrType
    case t if t =:= tPtrBoolean => BoolPtrType
    case t if t =:= tPtrInt     => IntPtrType
    case t if t =:= tPtrLong    => LongPtrType
    case t if t =:= tPtrFloat   => FloatPtrType
    case t if t =:= tPtrDouble  => DoublePtrType
    case t if t =:= tRawPtr     => VoidPtr
    case t if t <:< tPtr        => VoidPtr
    case t if t <:< tCxxObject  => ClassType(genCxxExternalType(t))
    case t if t <:< tCEnum      => EnumType(genCxxEnumType(t))
    case t if t <:< tResultValue     => ClassType(genCxxExternalType(t.typeArgs.head))
    case t if t <:< tResultPtr  => t.typeArgs.head match {
      case arg if arg <:< tBoolean => BoolPtrType
      case arg if arg <:< tInt     => IntPtrType
      case arg if arg <:< tLong    => LongPtrType
      case arg if arg <:< tFloat   => FloatPtrType
      case arg if arg <:< tDouble  => DoublePtrType
      case arg if arg <:< tCString => CStringPtrType
      case arg => ClassType(genCxxExternalType(arg))
    }
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
      data.cxxType
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

  protected def findCxxBody(scalaDef: DefDef): Option[String] = {
    findAnnotation(scalaDef.mods.annotations,"scala.scalanative.cxx.cxxBody")
      .flatMap(annot => extractAnnotationParameters(annot,Seq("body")).apply("body").flatMap(extractStringConstant))
  }

  protected def isDelete(m: DefDef): Boolean =
    findAnnotation(m.mods.annotations,"scala.scalanative.cxx.delete").isDefined

  protected def returnsConst(m: DefDef): Boolean =
    findAnnotation(m.mods.annotations,"scala.scalanative.cxx.returnsConst").isDefined

  protected def returnsRef(m: DefDef): Boolean =
    findAnnotation(m.mods.annotations,"scala.scalanative.cxx.returnsRef").isDefined

  protected def isRef(p: ValDef): Boolean =
    findAnnotation(p.mods.annotations,"scala.scalanative.cxx.ref").isDefined

  protected def isResultValue(tree: Tree): Boolean =
    try{
      getType(tree) <:< tResultValue
    } catch {
      case _:Throwable => false
    }
}

