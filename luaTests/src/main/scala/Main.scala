import lua.LuaState

import scalanative._
import unsafe._

object Main {
  def main(args: Array[String]): Unit = {
    val state = LuaState()
    state.openLibs()
//    state.doFile("hello.lua")
    state.doString("x = 42")
    println(state.readGlobalInteger("x"))
//    println(state.loadString(c"print 'A'\n"))
//    println(state.pcall(0,lua.LUA_MULTRET,0))
    state.free()
    println("DONE")
  }
}
