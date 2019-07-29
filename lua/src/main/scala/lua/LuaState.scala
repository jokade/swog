package lua

import lua.LuaState.{FunctionTable, ScalaModule}

import scalanative._
import unsafe._
import cobj._
import scala.collection.mutable
import scala.scalanative.interop.AutoReleasable
import scala.scalanative.runtime.{Intrinsics, RawPtr}

@CObj(prefix = "lua_", namingConvention = NamingConvention.LowerCase)
class LuaState extends AutoReleasable {
  private val registeredModules = mutable.Map.empty[String,ScalaModule]

  // store the pointer to this instance in the raw C lua state so that we can retrieve it by LuaState.getInstance()
  Intrinsics.storeObject(LuaState.getExtraSpace(this),this)

  @name("luaL_loadstring")
  def loadString(str: CString): LuaResult = extern
  def loadString(str: String): LuaResult = Zone{ implicit z => loadString(toCString(str)) }

  def doString(str: CString): LuaResult = loadString(str) | pcall(0,LUA_MULTRET,0)
  def doString(str: String): LuaResult = Zone{ implicit z => doString(toCString(str)) }

  @name("luaL_loadfilex")
  def loadFileX(filename: CString, mode: CString): LuaResult = extern
  def loadFile(filename: CString): LuaResult = loadFileX(filename,null)
  def loadFile(filename: String): LuaResult = Zone{ implicit z => loadFile(toCString(filename)) }

  def doFile(filename: CString): LuaResult = loadFile(filename) | pcall(0,LUA_MULTRET,0)
  def doFile(filename: String): LuaResult = Zone{ implicit z => doFile(toCString(filename)) }

  @name("luaL_openlibs")
  def openLibs(): Unit = extern

  def pcall(nargs: Int, nresults: Int, msgh: Int): LuaResult = pcallk(nargs,nresults,msgh,0,null)
  def pcallk(nargs: Int, nresults: Int, msgh: Int, ctx: KContext, k: KFunction): LuaResult = extern

  @name("lua_type")
  def getType(idx: Int): LuaType = extern

  @inline final def isNil(idx: Int): Boolean = getType(idx) == LuaType.NIL
  @inline final def isBoolean(idx: Int): Boolean = getType(idx) == LuaType.BOOLEAN
  def isInteger(idx: Int): Boolean = extern
  def isNumber(idx: Int): Boolean = extern
  def isString(idx: Int): Boolean = extern

  def pushBoolean(b: Boolean): Unit = extern
  @name("lua_toboolean")
  def getBoolean(idx: Int): Boolean = extern

  def pushInteger(i: LuaInteger): Unit = extern
  @name("lua_tointegerx")
  def getIntegerX(idx: Int)(implicit isnum: ResultPtr[Boolean]): LuaInteger = extern
  def getInteger(idx: Int): LuaInteger = getIntegerX(idx)(null)

  def pushNumber(n: LuaNumber): Unit = extern
  @name("lua_tonumberx")
  def getNumberX(idx: Int)(implicit isnum: ResultPtr[Int]): LuaNumber = extern
  def getNumber(idx: Int): LuaNumber = getNumberX(idx)(null)

  def pushString(s: CString): CString = extern
  def pushString(s: String): Unit = Zone{ implicit z => pushString(toCString(s)) }
  @name("lua_tolstring")
  def getCString(idx: Int, len: CSize): CString = extern
  def getCString(idx: Int): CString = getCString(idx,0)
  def getString(idx: Int): String = fromCString(getCString(idx))

  def pushNil(): Unit = extern

  def pushCClosure(f: LuaCFunction, n: Int): Unit = extern
  @inline final def pushCFunction(f: LuaCFunction): Unit = pushCClosure(f,0)
  def isCFunction(idx: Int): Boolean = extern

  def getTop: Int = extern
  def setTop(idx: Int): Unit = extern

  def getGlobal(name: CString): LuaType = extern
  def getGlobal(name: String): LuaType = Zone{ implicit z => getGlobal(toCString(name)) }

  def setGlobal(name: CString): Unit = extern
  def setGlobal(name: String): Unit = Zone{ implicit z => setGlobal(toCString(name)) }

  def createTable(narr: Int, nrec: Int): Unit = extern
  def newTable(): Unit = createTable(0,0)

  def getTable(idx: Int): LuaType = extern
  def getField(idx: Int, key: CString): LuaType = {
    pushString(key)
    getTable(idx)
  }
  def getField(idx: Int, key: String): LuaType = {
    pushString(key)
    getTable(idx)
  }

  def writeGlobal(name: String, value: Any): Unit = {
    value match {
      case i: LuaInteger =>
        pushInteger(i)
      case i: Int =>
        pushInteger(i)
      case n: LuaNumber =>
        pushNumber(n)
      case f: Float =>
        pushNumber(f)
      case b: Boolean =>
        pushBoolean(b)
      case str: String =>
        pushString(str)
      case _ => throw new LuaState.Exception("unsupported value: "+value)
    }
    setGlobal(name)
  }

  def readGlobalInteger(name: String): Option[LuaInteger] = {
    getGlobal(name)
    if(isInteger(-1))
      Some( getInteger(-1) )
    else
      None
  }

  def readGlobalNumber(name: String): Option[LuaNumber] = {
    getGlobal(name)
    if(isNumber(-1))
      Some( getNumber(-1) )
    else
      None
  }

  def readGlobalCString(name: String): Option[CString] = {
    getGlobal(name)
    if(isString(-1))
      Some(getCString(-1))
    else
      None
  }

  def readGlobalString(name: String): Option[String] = readGlobalCString(name).map(fromCString(_))


  @name("luaL_setfuncs")
  def setFuncs(funcs: Ptr[LuaReg]): Unit = extern

  @name("luaL_checkinteger")
  def checkInteger(arg: Int): LuaInteger = extern

  @name("luaL_checknumber")
  def checkNumber(arg: Int): LuaNumber = extern

  def checkBoolean(arg: Int): Boolean = getBoolean(arg)

  @name("luaL_checklstring")
  def checkLString(arg: Int)(len: ResultPtr[CSize]): CString = extern
  def checkString(arg: Int): String = fromCString(checkLString(arg)(null))

  @name("lua_close")
  override def free(): Unit = extern

  /**
   * Creates a new Lua table with the specified functions as members on the top of the stack.
   *
   * @param funcs name/function pairs
   */
  def createModule(funcs: FunctionTable): Unit = Zone{ implicit z =>
    val seq = funcs.toSeq
    val size = seq.size
    val arr = alloc[LuaReg](size+1)
    for(i <- 0 until size) {
      val (name,f) = seq(i)
      arr(i)._1 = toCString(name)
      arr(i)._2 = f
    }
    arr(size)._1 = null
    arr(size)._2 = null
    newTable()
    setFuncs(arr)
  }

  def registerScalaModule(module: ScalaModule): Unit =
    if(registeredModules.contains(module.name))
      throw new LuaState.Exception(s"module '${module.name}' already registered!")
    else {
     registeredModules += module.name -> module
    }

  def registerModule(module: LuaModule): Unit = registerScalaModule(module.__lua.module)

  def loadScalaUtils(): Unit = {
    createModule(LuaState._scalaUtils)
    setGlobal("scala")
  }
}

object LuaState {
  type FunctionTable = Iterable[(String,LuaCFunction)]

  class Exception(msg: String) extends RuntimeException(msg)

  case class ScalaModule(name: String, funcs: FunctionTable)

  @inline private def getExtraSpace(l: LuaState): RawPtr = getExtraSpace(runtime.toRawPtr(l.__ptr))
  @inline private def getExtraSpace(l: RawPtr): RawPtr = Intrinsics.elemRawPtr(l,-sizeof[Ptr[Byte]])
  @inline def getInstance(l: RawPtr): LuaState = Intrinsics.loadObject(getExtraSpace(l)).asInstanceOf[LuaState]

  private val _loadModule = new CFuncPtr1[RawPtr,Int] {
    override def apply(l: RawPtr): Int = {
      val state = getInstance(l)
      val name = state.checkString(1)
      state.registeredModules.get(name) match {
        case Some(m) =>
          state.createModule(m.funcs)
          1
        case None => 0
      }
    }
  }

  private val _scalaUtils = Map(
    "load" -> _loadModule
  )

  @name("luaL_newstate")
  def apply(): LuaState = extern

}