package lua

import scala.scalanative.unsafe._

trait LuaModule { self: Singleton =>
  def __lua: LuaModule.Data
}
object LuaModule {
  abstract class Data {
    def funcTable: LuaState.FunctionTable
    def methodTable: LuaState.FunctionTable
    def module: LuaState.ScalaModule
    def metaTableName: CString
    final def loadMetaTable(state: LuaState): Unit =
      if( state.newMetaTable(metaTableName) ) Zone{ implicit z =>
        state.pushValue(-1)
        state.setField(-2,c"__index")
        state.setFuncs(methodTable)
      }
      else {
//        state.pop(1)
      }

    final def push(state: LuaState, obj: Object): Unit = {
      state.newUserData(obj)
      loadMetaTable(state)
      state.setMetaTable(-2)
    }
  }
}
