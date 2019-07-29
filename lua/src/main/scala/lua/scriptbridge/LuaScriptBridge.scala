package lua.scriptbridge

import de.surfice.smacrotools.MacroAnnotationHandler

import scala.reflect.macros.whitebox
import scala.scalanative.scriptbridge.internal.ScriptBridgeHandler

class LuaScriptBridge(val c: whitebox.Context) extends ScriptBridgeHandler {
  import c.universe._

  private val tUnit = weakTypeOf[Unit]
  private val tInt = weakTypeOf[Int]
  private val tLong = weakTypeOf[Long]
  private val tBoolean = weakTypeOf[Boolean]
  private val tFloat = weakTypeOf[Float]
  private val tDouble = weakTypeOf[Double]
  private val tString = weakTypeOf[String]

  override def language: String = "lua"

  override def transform: Transformation = {
    case obj: ObjectTransformData =>
      implicit val data = ScriptBridgeData(obj.data)
      obj.addStatements(genLua())
    case default => default
  }

  private def genLua()(implicit data: ScriptBridgeData): Tree = {
    val wrappers = genLuaWrappers()
    val funcMap = wrappers.map{ p =>
      q"${Literal(Constant(p._1))} -> ${p._2}"
    }
    val funcs = wrappers.map(_._3)
    val scalaModule = genScalaModule()
    q"""object __lua extends lua.LuaModule.Data {
          import scalanative.unsafe._
          import scala.scalanative.runtime.RawPtr
          ..$funcs
          val funcTable = Map( ..$funcMap )
            ..$scalaModule
        }"""
  }

  private def genScalaModule()(implicit data: ScriptBridgeData): Tree = {
    val moduleName = Literal(Constant(data.sbModuleName))
    q"val module = lua.LuaState.ScalaModule($moduleName,funcTable)"
  }

  private def genLuaWrappers()(implicit data: ScriptBridgeData): Seq[(String,TermName,Tree)] =
    data.sbExportFunctions.map(genLuaFunctionWrapper)

  private def genLuaFunctionWrapper(scalaDef: DefDef)(implicit data: ScriptBridgeData): (String,TermName,Tree) = {
    val (luaName, termName) = genLuaFunctionWrapperName(scalaDef)
    val call = genScalaFunctionCall(scalaDef)
    val func =
      q"""val ${termName} = new CFuncPtr1[RawPtr,Int]{
            def apply(L: RawPtr): Int = $call
          }"""
    (luaName, termName, func)
  }

  private def genLuaFunctionWrapperName(scalaDef: DefDef)(implicit data:ScriptBridgeData): (String,TermName) = {
    val luaName = scalaDef.name.toString()
    val termName = TermName("f_"+luaName)
    (luaName,termName)
  }

  private def genScalaFunctionCall(scalaDef: DefDef)(implicit data: ScriptBridgeData): Tree = {
    val ret = genReturn(scalaDef)
    val (args,argDefs) = genArgs(scalaDef).unzip
    q"""val state = lua.LuaState.getInstance(L)
          ..$argDefs
        val res = ${scalaDef.name}(..$args)
        $ret
        1"""
  }

  private def genArgs(scalaDef: DefDef)(implicit data: ScriptBridgeData): Seq[(Tree,Tree)] =
    scalaDef.vparamss match {
      case Nil => Nil
      case List(args) => args.zipWithIndex.map( p => genArg(p._1,p._2+1))
    }

  private def genArg(v: ValDef, idx: Int)(implicit data: ScriptBridgeData): (Tree,Tree) = {
    val argIdx = Literal(Constant(idx))
    val argName = TermName("arg"+idx)
    q"$argName" -> (getLuaType(v.tpt) match {
      case LuaInt =>
        q"val $argName = state.checkInteger($argIdx).toInt"
      case LuaLong =>
        q"val $argName = state.checkInteger($argIdx)"
      case LuaFloat =>
        q"val $argName = state.checkNumber($argIdx).toFloat"
      case LuaDouble =>
        q"val $argName = state.checkNumber($argIdx)"
      case LuaString =>
        q"val $argName = state.checkString($argIdx)"
      case LuaBoolean =>
        q"val $argName = state.checkBoolean($argIdx)"
      case LuaNil => ???
    })
  }

  private def genReturn(scalaDef: DefDef)(implicit data: ScriptBridgeData): Tree = {
    getLuaType(scalaDef.tpt) match {
      case LuaInt | LuaLong =>
        q"state.pushInteger(res)"
      case LuaFloat | LuaDouble =>
        q"state.pushNumber(res)"
      case LuaBoolean =>
        q"state.pushBoolean(res)"
      case LuaNil =>
        q"state.pushNil()"
      case LuaString =>
        q"state.pushString(res)"
    }
  }

  sealed trait LuaType
  case object LuaNil extends LuaType
  case object LuaBoolean extends LuaType
  case object LuaInt extends LuaType
  case object LuaLong extends LuaType
  case object LuaFloat extends LuaType
  case object LuaDouble extends LuaType
  case object LuaString extends LuaType

  private def getLuaType(tpt: Tree)(implicit data: ScriptBridgeData): LuaType =
    getType(tpt,true) match {
      case t if t =:= tInt     => LuaInt
      case t if t =:= tLong    => LuaLong
      case t if t =:= tFloat   => LuaFloat
      case t if t =:= tDouble  => LuaDouble
      case t if t =:= tBoolean => LuaBoolean
      case t if t =:= tUnit    => LuaNil
      case t if t =:= tString  => LuaString
      case _ => ???
    }
}
