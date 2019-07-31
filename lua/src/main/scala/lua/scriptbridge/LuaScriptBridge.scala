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
  private val tpeLuaWrapper = tq"lua.LuaWrapper"

  override def language: String = "lua"

  override def transform: Transformation = {
    case cls: ClassTransformData =>
      val updParents = cls.modParts.parents :+ tpeLuaWrapper
      val luaPush = q"override def __luaPush(state: lua.LuaState): Unit = ${cls.modParts.companion.get.name}.__lua.push(state,this)"
      cls
        .updParents(updParents)
        .addStatements(luaPush)
    case obj: ObjectTransformData =>
      implicit val data = ScriptBridgeData(obj.data)
      obj.addStatements(genLua())
    case default => default
  }

  private def genLua()(implicit data: ScriptBridgeData): Tree = {
    val funcWrappers = genLuaFunctionWrappers()
    val methodWrappers = genLuaMethodWrappers()
    val wrappers = funcWrappers ++ methodWrappers
    val funcMap = funcWrappers.map{ p =>
      q"${Literal(Constant(p._1))} -> ${p._2}"
    }
    val methodMap = methodWrappers.map{ p =>
      q"${Literal(Constant(p._1))} -> ${p._2}"
    }
    val defns = wrappers.map(_._3)
    val scalaModule = genScalaModule()
    val metaTableName = Literal(Constant(data.sbModuleName))
    q"""object __lua extends lua.LuaModule.Data {
          import scalanative.unsafe._
          import scala.scalanative.runtime.RawPtr
          ..$defns
          val funcTable = Map( ..$funcMap )
          val methodTable = Map( ..$methodMap )
          val metaTableName = CQuote(StringContext($metaTableName)).c()
            ..$scalaModule
        }"""
  }

  private def genScalaModule()(implicit data: ScriptBridgeData): Tree = {
    val moduleName = Literal(Constant(data.sbModuleName))
    q"val module = lua.LuaState.ScalaModule($moduleName,funcTable)"
  }

  private def genLuaFunctionWrappers()(implicit data: ScriptBridgeData): Seq[(String,TermName,Tree)] =
    data.sbExportFunctions.map(genLuaFunctionWrapper) ++
      genConstructorWrapper()

  private def genLuaMethodWrappers()(implicit data: ScriptBridgeData): Seq[(String,TermName,Tree)] =
      data.sbExportMethods.map(genLuaMethodWrapper)

  private def genLuaFunctionWrapper(scalaDef: DefDef)(implicit data: ScriptBridgeData): (String,TermName,Tree) = {
    val (luaName, termName) = genLuaFunctionWrapperName(scalaDef)
    val call = genScalaFunctionCall(scalaDef)
    val func =
      q"""val ${termName} = new CFuncPtr1[RawPtr,Int]{
            def apply(L: RawPtr): Int = $call
          }"""
    (luaName, termName, func)
  }

  private def genLuaMethodWrapper(scalaDef: DefDef)(implicit data: ScriptBridgeData): (String,TermName,Tree) = {
    val (luaName, termName) = genLuaMethodWrapperName(scalaDef)
    val call = genScalaMethodCall(scalaDef)
    val method =
      q"""val ${termName} = new CFuncPtr1[RawPtr,Int]{
            def apply(L: RawPtr): Int = $call
          }"""
    (luaName, termName, method)
  }

  private def genLuaFunctionWrapperName(scalaDef: DefDef)(implicit data:ScriptBridgeData): (String,TermName) = {
    val luaName = scalaDef.name.toString()
    val termName = TermName("f_"+luaName)
    (luaName,termName)
  }

  private def genLuaMethodWrapperName(scalaDef: DefDef)(implicit data: ScriptBridgeData): (String,TermName) = {
    val luaName = scalaDef.name.toString()
    val termName = TermName("m_"+luaName)
    (luaName,termName)
  }

  private def genScalaFunctionCall(scalaDef: DefDef)(implicit data: ScriptBridgeData): Tree = {
    val ret = genReturn(scalaDef)
    val (args,argDefs) = genArgs(scalaDef)
    val call = args match {
      case Nil => q"val res = ${scalaDef.name}"
      case List(args) => q"val res = ${scalaDef.name}(..$args)"
    }
    q"""val state = lua.LuaState.getInstance(L)
        ..$argDefs
        $call
        $ret
        1"""
  }

  private def genScalaMethodCall(scalaDef: DefDef)(implicit data: ScriptBridgeData): Tree = {
    val ret = genReturn(scalaDef)
    val (args,argDefs) = genArgs(scalaDef,offset = 1)
    val call = args match {
      case Nil => q"val res = obj.${scalaDef.name}"
      case List(args) => q"val res = obj.${scalaDef.name}(..$args)"
    }
    q"""val state = lua.LuaState.getInstance(L)
        val obj = runtime.Intrinsics.loadObject(state.checkUserData(1,metaTableName)).asInstanceOf[${data.sbScalaType.get}]
        ..$argDefs
        $call
        $ret
        1
     """
  }

  private def genConstructorWrapper()(implicit data: ScriptBridgeData): Option[(String,TermName,Tree)] = data.sbConstructor.map{ constr =>
    val (args,argDefs) = genArgs(constr.params,0)
    val call = args match {
      case Nil => q"val res = new ${constr.name}"
      case List(args) => q"val res = new ${constr.name}(..$args)"
    }
    val func =
      q"""val f_new = new CFuncPtr1[RawPtr,Int] {
          def apply(L: RawPtr): Int = {
            val state = lua.LuaState.getInstance(L)
            ..$argDefs
            $call
            state.pushUserData(res)
          1
       }}"""
    ("new",TermName("f_new"),func)
  }

  private def genArgs(scalaDef: DefDef, offset: Int = 0)(implicit data: ScriptBridgeData): (Seq[Seq[Tree]],Seq[Tree]) =
    scalaDef.vparamss match {
      case Nil => (Nil,Nil)
      case List(xs) =>
        val (args,argDefs) = genArgs(xs,offset)
        (Seq(args),argDefs)
    }

  private def genArgs(args: Seq[ValDef], offset: Int)(implicit data: ScriptBridgeData): (Seq[Tree],Seq[Tree]) =
    args.zipWithIndex.map( p => genArg(p._1,p._2+offset+1)).unzip

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
      case LuaUserObj =>
        q"val $argName = runtime.Intrinsics.loadObject(state.toUserData($argIdx)).asInstanceOf[${v.tpt}]"
      case LuaNil => ???
    })
  }

  private def genReturn(scalaDef: DefDef)(implicit data: ScriptBridgeData): Tree =
    genReturn( getLuaType(scalaDef.tpt) )

  private def genReturn(tpe: LuaType): Tree = tpe match {
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
    case LuaUserObj =>
      q"state.pushUserData(res)"
  }

  sealed trait LuaType
  case object LuaNil extends LuaType
  case object LuaBoolean extends LuaType
  case object LuaInt extends LuaType
  case object LuaLong extends LuaType
  case object LuaFloat extends LuaType
  case object LuaDouble extends LuaType
  case object LuaString extends LuaType
  case object LuaUserObj extends LuaType

  private def getLuaType(tpt: Tree)(implicit data: ScriptBridgeData): LuaType =
    getType(tpt,true) match {
      case t if t =:= tInt     => LuaInt
      case t if t =:= tLong    => LuaLong
      case t if t =:= tFloat   => LuaFloat
      case t if t =:= tDouble  => LuaDouble
      case t if t =:= tBoolean => LuaBoolean
      case t if t =:= tUnit    => LuaNil
      case t if t =:= tString  => LuaString
      case _ => LuaUserObj
    }
}
