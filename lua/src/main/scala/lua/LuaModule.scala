package lua

trait LuaModule { self: Singleton =>
  def __lua: LuaModule.Data
}
object LuaModule {
  abstract class Data {
    def funcTable: LuaState.FunctionTable
    def module: LuaState.ScalaModule
  }
}
