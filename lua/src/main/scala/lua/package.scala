import scala.scalanative.runtime.RawPtr
import scalanative.unsafe._

package object lua {

  type LuaResult = Int
  object LuaResult {
    val OK      = 0
    val ERRRUN  = 1
    val ERRMEM  = 2
    val ERRERR  = 3
    val ERRGCMM = 4
  }

  val LUA_MULTRET = -1

  type LuaType = Int
  object LuaType {
    val NONE              = -1
    val NIL               = 0
    val BOOLEAN           = 1
    val LIGHTUSERDATA     = 2
    val NUMBER            = 3
    val STRING            = 4
    val TABLE             = 5
    val FUNCTION          = 6
    val USERDATA          = 7
    val THREAD            = 8
  }

  type KContext = Int
  type KFunction = CFuncPtr3[Ptr[Byte],Int,KContext,Int]

  type LuaNumber = CDouble
  type LuaInteger = CLongLong
  type LuaUnsigned = CUnsignedLongLong
  type LuaCFunction = CFuncPtr1[RawPtr,Int]

  type LuaReg = CStruct2[CString,LuaCFunction]
}
