package scala.scalanative.objc

import de.surfice.smacrotools.CommonMacroTools

import scala.language.reflectiveCalls
import scala.reflect.macros.TypecheckException
import scala.scalanative.cobj.internal.CommonHandler
import scala.scalanative.unsafe.Ptr
import scala.scalanative.unsigned.{UByte, UInt, ULong, UShort}

trait ObjCMacroTools extends CommonMacroTools {
  import c.universe._

  // see https://developer.apple.com/library/archive/documentation/Cocoa/Conceptual/ObjCRuntimeGuide/Articles/ocrtTypeEncodings.html
  sealed abstract class TypeCode(val code: String, // the Objective-C type code
                                 val suffix: String // suffix used to generate distinctive names for msgSend signatures
                                 ) {
    override def toString(): String = suffix
  }
  object TypeCode {
    object byte extends TypeCode("c","y")
    object ubyte extends TypeCode("C","Y")
    object char extends TypeCode("c","c")
    object uchar extends TypeCode("C","C")
    object short extends TypeCode("s","s")
    object ushort extends TypeCode("S","S")
    object int extends TypeCode("i","i")
    object uint extends TypeCode("I","I")
    object long extends TypeCode("l","l")
    object ulong extends TypeCode("L","L")
    object longlong extends TypeCode("q","q")
    object ulonglong extends TypeCode("Q","Q")
    object float extends TypeCode("f","f")
    object double extends TypeCode("d","d")
    object bool extends TypeCode("B","B")
    object void extends TypeCode("v","v")
    object string extends TypeCode("*","a")
    object obj extends TypeCode("@","p")
  }

  implicit class MacroData(var data: Map[String, Any]) {
    type Data = Map[String, Any]
    type Selectors = Seq[(String,TermName)]
    type Externals = Set[External]
    type Statements = Seq[Tree]

    def companionName: TermName = data.getOrElse("companionName",null).asInstanceOf[TermName]
    def withCompanionName(name: TermName): Data = data.updated("companionName",name)

    // selectors to be defined in the companion object
    def selectors: Selectors = data.getOrElse("selectors", Nil).asInstanceOf[Selectors]
    def withSelectors(selectors: Selectors): Data = data.updated("selectors",selectors)

    def selectors_=(selectors: Selectors): Data = {
      data += "selectors" -> selectors
      data
    }

    // external declarations to be added to the companion object
    def externals: Externals = data.getOrElse("externals",Nil).asInstanceOf[Externals]
    def withExternals(externals: Externals): Data = data.updated("externals",externals)

    // statements to be executed during ObjC class intialization for @ScalaDefined classes
    // (i.e. the code required to define the ObjC class when the first call to a class method is issued)
    def objcClassInits: Statements = data.getOrElse("objcClassInits", Nil).asInstanceOf[Statements]

    def objcClassInits_=(stmts: Statements): Data = {
      data += "objcClassInits" -> stmts
      data
    }

    def additionalCompanionStmts: Statements = data.getOrElse("compStmts", Nil).asInstanceOf[Statements]
    def additionalCompanionStmts_=(stmts: Statements): Data = {
      data += "compStmts" -> stmts
      data
    }
    def withAdditionalCompanionStmts(stmts: Statements): Data = data.updated("compStmts",stmts)

    def replaceClassBody: Option[Statements] = data.getOrElse("replaceClsBody", None).asInstanceOf[Option[Statements]]
    def replaceClassBody_=(stmts: Option[Statements]): Data = {
      data += "replaceClsBody" -> stmts
      data
    }
  }

//  protected[this] val ccastImport = q"import scalanative.unsafe.CCast"
  protected[this] val ccastImport = q""
  protected[this] val clsTarget = TermName("__cls")

  protected[this] def cstring(s: String) = q"scalanative.unsafe.CQuote(StringContext($s)).c()"
  protected[this] val tObjCObject = c.weakTypeOf[ObjCObject]
  protected[this] val tFloat = c.weakTypeOf[Float]
  protected[this] val tDouble = c.weakTypeOf[Double]
  protected[this] val tBoolean = c.weakTypeOf[Boolean]
  protected[this] val tByte = c.weakTypeOf[Byte]
  protected[this] val tUByte = c.weakTypeOf[UByte]
  protected[this] val tShort = c.weakTypeOf[Short]
  protected[this] val tUShort = c.weakTypeOf[UShort]
  protected[this] val tInt = c.weakTypeOf[Int]
  protected[this] val tUInt = c.weakTypeOf[UInt]
  protected[this] val tLong = c.weakTypeOf[Long]
  protected[this] val tULong = c.weakTypeOf[ULong]
  protected[this] val tUnit = c.weakTypeOf[Unit]
  protected[this] val tPtr = c.weakTypeOf[Ptr[_]]
  protected[this] val tChar = c.weakTypeOf[Char]
  protected[this] val tAnyVal = c.weakTypeOf[AnyVal]

  protected[this] val tpeObjCObject = tq"$tObjCObject"

  private val tpePtr = tq"scalanative.unsafe.Ptr[Byte]"

  protected[this] val msgSendNameAnnot = Modifiers(NoFlags,typeNames.EMPTY,List(q"new name(${Literal(Constant("objc_msgSend"))})"))
  protected[this] val msgSendFpretNameAnnot = Modifiers(NoFlags,typeNames.EMPTY,List(q"new name(${Literal(Constant("objc_msgSend_fpret"))})"))

  protected[this] def genSelector(name: TermName, args: List[List[ValDef]]): (String, TermName) = {
    val s = genSelectorString(name, args)
    (s, genSelectorTerm(s))
  }

  protected[this] def genSelectorTerm(name: TermName, args: List[List[ValDef]]): TermName =
    genSelectorTerm(genSelectorString(name,args))

  protected[this] def genSelectorTermString(selectorString: String): String =
    "__sel_"+selectorString.replaceAll(":","_")

  protected[this] def genSelectorTerm(selectorString: String): TermName = {
    TermName(genSelectorTermString(selectorString))
  }

  // TODO: handle arguments!
  protected[this] def genSelectorString(method: MethodSymbol): String = method.name.toString

  protected[this] def genSelectorString(name: TermName, args: List[List[ValDef]]): String =
    name.toString.replaceAll("_",":")

  private def selectorMethodName(name: TermName): String = {
    val s = name.toString
    if(s.endsWith("$eq"))
      "set" + s.head.toUpper + s.tail.stripSuffix("_$eq")
    else
      s
  }


  protected[this] def genSelectorDef(selector: String, selectorTerm: TermName) =
    q"protected lazy val $selectorTerm = scalanative.objc.runtime.sel_registerName(scalanative.unsafe.CQuote(StringContext($selector)).c())"

  protected[this] def getObjCType(tpt: Tree): Option[Type] =
    try {
      val typed = getType(tpt,true)
      Some(typed)
    } catch {
      case ex: TypecheckException =>
        None
    }

  protected[this] def isObjCObject(tpt: Tree): Boolean = isObjCObject(getObjCType(tpt))

  protected[this] def isObjCObject(tpe: Option[Type]): Boolean = tpe match {
    case Some(t) => t.baseClasses.map(_.asType.toType).exists( t => t <:< tObjCObject )
    case _ => true
  }

  protected[this] def wrapResult(result: Tree, resultType: Tree): Tree = {
    val tpe = getObjCType(resultType)
    wrapResult(result,tpe)
  }

  protected def wrapResult(result: Tree, resultType: Option[Type]): Tree = {
    resultType match {
    case Some(t) if t <:< tByte  || t <:< tUByte  || t <:< tShort || t <:< tUShort || t <:< tInt || t <:< tUInt ||
                    t <:< tLong  || t <:< tULong  || t <:< tFloat || t <:< tDouble ||
                    t <:< tChar  || t <:< tBoolean || t <:< tUnit =>
      result
    case Some(t) if t <:< tPtr =>
      q"scalanative.runtime.fromRawPtr($result)"
    case _ =>
      println("result: "+result)
      q"{val r = $result; if(r==null) null else new ${resultType.get}(scalanative.runtime.fromRawPtr(r))}"
  }
  }

  private def genTypeCode(tpt: Tree): TypeCode =
    try{
      getType(tpt, true).dealias match {
        case t if t <:< tByte => TypeCode.byte
        case t if t <:< tUByte => TypeCode.ubyte
        case t if t <:< tShort => TypeCode.short
        case t if t <:< tUShort => TypeCode.ushort
        case t if t <:< tInt => TypeCode.int
        case t if t <:< tUInt => TypeCode.uint
        case t if t <:< tLong => TypeCode.long
        case t if t <:< tULong => TypeCode.ulong
        case t if t <:< tBoolean => TypeCode.bool
        case t if t <:< tChar => TypeCode.char
        case t if t <:< tDouble => TypeCode.double
        case t if t <:< tFloat => TypeCode.float
        case t if t <:< tObjCObject || t <:< tPtr => TypeCode.obj
        case t if t <:< tUnit => TypeCode.void
        case _ =>
          c.error(c.enclosingPosition, s"unsupported type: $tpt")
          ???
      }
    } catch {
      // if type checking fails we simply assume that we're handling an ObjC object
      case ex: TypecheckException => TypeCode.obj //"p"
    }

  private def mapTypeForExternalCall(tpt: Tree): Tree = getType(tpt,true) match {
    case t if t <:< tAnyVal || t <:< tUByte || t <:< tUShort || t <:< tUInt || t <:< tULong => tpt
    case t if t <:< tObjCObject || t <:< tPtr => tpePtr
    case _ =>
      c.error(c.enclosingPosition,s"unsupported type: $tpt")
      ???
  }

  protected[this] def genNameWithTypeCodes(prefix: String)(scalaDef: DefDef): TermName = {
    val suffix = scalaDef.vparamss match {
      case Nil => ""
      case List(argdefs) => argdefs.map(p => genTypeCode(p.tpt)).mkString
      case List(inargs,outargs) => ""
      case x =>
        c.error(c.enclosingPosition, "multiple parameter lists not supported for ObjC classes")
        ???
    }
    val retType = genTypeCode(scalaDef.tpt)
    TermName(prefix+retType+suffix)
  }

  protected[this] def genMsgSendName = genNameWithTypeCodes("msgSend_") _


  protected[this] def genMsgSend(scalaDef: DefDef): External = {
    val name = genMsgSendName(scalaDef)
    val args = scalaDef.vparamss match {
      case Nil => Nil
      case List(argdefs) => argdefs.map( p => mapTypeForExternalCall(p.tpt)).zipWithIndex.map { t =>
        val name = TermName("arg"+t._2)
        q"val $name: ${t._1}"
      }
    }
    genTypeCode(scalaDef.tpt) match {
      case TypeCode.byte =>
        External(name.toString)(q"$msgSendNameAnnot def $name(self: id, sel: SEL, ..$args): Byte = extern" )
      case TypeCode.ubyte =>
        External(name.toString)(q"$msgSendNameAnnot def $name(self: id, sel: SEL, ..$args): scalanative.unsigned.UByte = extern" )
      case TypeCode.short =>
        External(name.toString)(q"$msgSendNameAnnot def $name(self: id, sel: SEL, ..$args): Short = extern" )
      case TypeCode.ushort =>
        External(name.toString)(q"$msgSendNameAnnot def $name(self: id, sel: SEL, ..$args): scalanative.unsigned.UShort = extern" )
      case TypeCode.int =>
        External(name.toString)(q"$msgSendNameAnnot def $name(self: id, sel: SEL, ..$args): Int = extern" )
      case TypeCode.uint =>
        External(name.toString)(q"$msgSendNameAnnot def $name(self: id, sel: SEL, ..$args): scalanative.unsigned.UInt = extern" )
      case TypeCode.long =>
        External(name.toString)(q"$msgSendNameAnnot def $name(self: id, sel: SEL, ..$args): Long = extern" )
      case TypeCode.ulong =>
        External(name.toString)(q"$msgSendNameAnnot def $name(self: id, sel: SEL, ..$args): scalanative.unsigned.ULong = extern" )
      case TypeCode.bool =>
        External(name.toString)(q"$msgSendNameAnnot def $name(self: id, sel: SEL, ..$args): Boolean = extern" )
      case TypeCode.double =>
        External(name.toString)(q"$msgSendFpretNameAnnot def $name(self: id, sel: SEL, ..$args): Double = extern" )
      case TypeCode.float =>
        External(name.toString)(q"$msgSendFpretNameAnnot def $name(self: id, sel: SEL, ..$args): Float = extern" )
      case TypeCode.char =>
        External(name.toString)(q"$msgSendNameAnnot def $name(self: id, sel: SEL, ..$args): Char = extern" )
      case TypeCode.void =>
        External(name.toString)(q"$msgSendNameAnnot def $name(self: id, sel: SEL, ..$args): Unit = extern" )
      case TypeCode.obj =>
        External(name.toString)(q"$msgSendNameAnnot def $name(self: id, sel: SEL, ..$args): scalanative.runtime.RawPtr = extern" )
    }
  }


  case class External(name: String)(val decl: Tree)
}
