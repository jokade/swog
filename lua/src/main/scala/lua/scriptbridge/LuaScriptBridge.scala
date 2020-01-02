package lua.scriptbridge

import de.surfice.smacrotools.MacroAnnotationHandler
import lua.{LuaTable, nolua}

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
  private val tMap = weakTypeOf[Map[String,Any]]
  private val tOption = weakTypeOf[Option[_]]
  private val tAny = weakTypeOf[Any]
  private val tLuaTable = weakTypeOf[LuaTable]
  private val tpeLuaWrapper = tq"lua.LuaWrapper"
  private val annotNoLua = weakTypeOf[nolua]

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
          import scala.scalanative.runtime.{Intrinsics,RawPtr}
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
    data.sbExportFunctions.filter(shouldExportToLua).flatMap(genLuaWrappers(false)) ++
      genConstructorWrapper()

  private def genLuaMethodWrappers()(implicit data: ScriptBridgeData): Seq[(String,TermName,Tree)] =
      (data.sbExportMethods++data.sbExportProperties).filter(shouldExportToLua).flatMap(genLuaWrappers(true))

  private def genLuaWrappers(isClassDefn: Boolean)(scalaDef: ValOrDefDef)(implicit data: ScriptBridgeData): Seq[(String,TermName,Tree)] =
    if(scalaDef.mods.hasFlag(Flag.MUTABLE))
      Seq(genLuaWrapper(scalaDef,isClassDefn,false), genLuaWrapper(scalaDef,isClassDefn,true))
    else
      Seq(genLuaWrapper(scalaDef,isClassDefn,false))

  private def genLuaWrapper(scalaDef: ValOrDefDef, isClassDefn: Boolean, isSetter: Boolean)(implicit data: ScriptBridgeData): (String,TermName,Tree) = {
    val (luaName, termName) = genLuaWrapperName(scalaDef,isClassDefn,isSetter)
    val call = genScalaCall(scalaDef,isClassDefn,isSetter)
    val defn =
      q"""val ${termName} = new CFuncPtr1[RawPtr,Int]{
            def apply(L: RawPtr): Int = $call
          }"""
    (luaName, termName, defn)
  }


  private def genLuaWrapperName(scalaDef: ValOrDefDef, isClassDefn: Boolean, isSetter: Boolean): (String,TermName) = {
    val name = findAnnotation(scalaDef.mods.annotations,"lua.luaname") match {
      case Some(annot) =>
        extractAnnotationParameters(annot,Seq("name")).apply("name").flatMap(extractStringConstant)
          .getOrElse(scalaDef.name.toString)
      case _ =>
        scalaDef.name.toString
    }
    val luaName =
      if(name.endsWith("_$eq")) {
        val n = name.stripSuffix("_$eq")
        "set" + n.head.toUpper + n.tail
      }
      else if(isSetter)
        "set" + name.head.toUpper + name.tail
      else
        name
    val termName =
      if(isClassDefn)
        TermName("m_"+luaName)
      else
       TermName("f_"+luaName)
    (luaName,termName)
  }

  private def genScalaCall(scalaDef: ValOrDefDef, isInstanceCall: Boolean, isSetter: Boolean)(implicit data: ScriptBridgeData): Tree = {
    val ret = genReturn(scalaDef,isSetter)
    val (args,argDefs) = scalaDef match {
      case defn: DefDef => genArgs(defn,if(isInstanceCall)1 else 0)
      case defn: ValDef if isSetter =>
        val idx = if(isInstanceCall) 2 else 1
        val (arg, argDef) = genArg(defn,idx)
        (Seq(Seq(arg)),Seq(argDef))
      case _ => (Nil,Nil)
    }
    val instance =
      if(isInstanceCall)
        q"""val obj = Intrinsics.loadObject(state.checkUserData(1,metaTableName)).asInstanceOf[${data.sbScalaType.get}]"""
      else
        q""
    val call = args match {
      case Nil =>
        if(isInstanceCall)
          q"val res = obj.${scalaDef.name}"
        else
          q"val res = ${scalaDef.name}"
      case List(xs) =>
        if(isSetter) {
          if(isInstanceCall)
            q"obj.${scalaDef.name} = arg2"
          else
            q"${scalaDef.name} = arg2"
        }
        else {
          if(isInstanceCall)
            q"val res = obj.${scalaDef.name}(..$xs)"
          else
            q"val res = ${scalaDef.name}(..$xs)"
        }
    }
    q"""val state = lua.LuaState.getInstance(L)
        $instance
        ..$argDefs
        $call
        $ret
        1"""
  }

  private def genConstructorWrapper()(implicit data: ScriptBridgeData): Option[(String,TermName,Tree)] = data.sbConstructor.flatMap{ constr =>
    if(constr.isPublic) {
      val (args, argDefs) = genArgs(constr.params, 0)
      val call = args match {
        case Nil => q"val res = new ${constr.name}"
        case List(xs) => q"val res = new ${constr.name}(..$xs)"
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
      Some(("new", TermName("f_new"), func))
    }
    else
      None
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
        q"val $argName = Intrinsics.loadObject(state.toUserData($argIdx)).asInstanceOf[${v.tpt}]"
      case LuaTable =>
        q"val $argName = state.table($argIdx)"
      case LuaMap =>
        q"val $argName = state.table($argIdx).toMap()"
      case LuaAny =>
        q"val $argName = state.getValue($argIdx)"
      case LuaOption =>
        q"val $argName = state.getValueOption($argIdx)"
      case LuaNil => ???
    })
  }

  private def genReturn(scalaDef: ValOrDefDef, isSetter: Boolean)(implicit data: ScriptBridgeData): Tree =
    if(isSetter)
      q"state.pushNil()"
    else
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
    case LuaAny =>
      q"state.pushValue(res)"
    case LuaMap =>
      q"state.pushMap(res)"
    case LuaOption =>
      q"state.pushOption(res)"
    case LuaTable =>
      c.error(c.enclosingPosition,"LuaTable is not allowed as a return value")
      ???
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
  case object LuaTable extends LuaType
  case object LuaAny extends LuaType
  case object LuaMap extends LuaType
  case object LuaOption extends LuaType

  private def getLuaType(tpt: Tree)(implicit data: ScriptBridgeData): LuaType =
    getType(tpt,true) match {
      case t if t =:= tInt      => LuaInt
      case t if t =:= tLong     => LuaLong
      case t if t =:= tFloat    => LuaFloat
      case t if t =:= tDouble   => LuaDouble
      case t if t =:= tBoolean  => LuaBoolean
      case t if t =:= tUnit     => LuaNil
      case t if t =:= tString   => LuaString
      case t if t =:= tLuaTable => LuaTable
      case t if t <:< tMap      => LuaMap
      case t if t <:< tOption   => LuaOption
      case t if t =:= tAny      => LuaAny
      case _ => LuaUserObj
    }

  private def shouldExportToLua(scalaDef: ValOrDefDef): Boolean =
    ! hasAnnotation(scalaDef.mods.annotations,annotNoLua)
}
