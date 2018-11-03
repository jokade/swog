package scala.scalanative.native.objc

import de.surfice.smacrotools.CommonMacroTools

import scala.language.reflectiveCalls
import scala.reflect.macros.TypecheckException
import scala.scalanative.native.objc.runtime.ObjCObject

trait ObjCMacroTools extends CommonMacroTools {
  import c.universe._

  implicit class MacroData(var data: Map[String, Any]) {
    type Data = Map[String, Any]
    type Selectors = Seq[(String, TermName)]
    type Statements = Seq[Tree]

    // selectors to be defined in the companion object
    def selectors: Selectors = data.getOrElse("selectors", Nil).asInstanceOf[Selectors]
    def withSelectors(selectors: Selectors): Data = data.updated("selectors",selectors)

    def selectors_=(selectors: Selectors): Data = {
      data += "selectors" -> selectors
      data
    }

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

  protected[this] val ccastImport = q"import scalanative.native.CCast"
  protected[this] val clsTarget = TermName("__cls")

  protected[this] def cstring(s: String) = q"scalanative.native.CQuote(StringContext($s)).c()"
  protected[this] val tObjCObject = c.weakTypeOf[ObjCObject]
  protected[this] val tFloat = c.weakTypeOf[Float]
  protected[this] val tDouble = c.weakTypeOf[Double]

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
  /*
    if(name.toString.contains("_"))
      name.toString.replaceAll("_",":")
    else args match {
      case Nil | List(Nil) => selectorMethodName(name)
      case List(args) => selectorMethodName(name) +: (args.tail map {
        case ValDef(_, name, _, _) => name.toString
      }) mkString("", ":", ":")
      case x =>
        c.error(c.enclosingPosition, "multiple parameter lists not supported for ObjC classes")
        ???
    }
  */


  private def selectorMethodName(name: TermName): String = {
    val s = name.toString
    if(s.endsWith("$eq"))
      "set" + s.head.toUpper + s.tail.stripSuffix("_$eq")
    else
      s
  }


  protected[this] def genSelectorDef(selector: String, selectorTerm: TermName) =
    q"protected lazy val $selectorTerm = scalanative.native.objc.runtime.sel_registerName(scalanative.native.CQuote(StringContext($selector)).c())"

  protected[this] def getObjCType(tpt: Tree): Option[Type] =
    try {
      val typed = getType(tpt,true)
      Some(typed)
    } catch {
      case ex: TypecheckException => None
    }

  protected[this] def isObjCObject(tpt: Tree): Boolean = isObjCObject(getObjCType(tpt))

  protected[this] def isObjCObject(tpe: Option[Type]): Boolean = tpe match {
    case Some(t) => t.baseClasses.map(_.asType.toType).exists( t => t <:< tObjCObject )
    case _ => true
  }

//  protected[this] def isAnyVal(tpe: Option[Type]): Boolean = tpe.exists(_ <:< tAnyVal)

  protected[this] def wrapResult(result: Tree, resultType: Tree): Tree = {
    val tpe = getObjCType(resultType)
    wrapResult(result,tpe)
  }

  protected[this] def wrapResult(result: Tree, resultType: Option[Type]): Tree = {
    if (isObjCObject(resultType))
      q"new ${resultType.get}($result)"
    else if (resultType.isDefined)
      q"$result.cast[${resultType.get}]"
    else
      q"$result"
  }



}
