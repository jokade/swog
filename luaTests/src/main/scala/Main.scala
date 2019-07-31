import de.surfice.smacrotools.debug
import lua.{LuaModule, LuaReg, LuaState}

import scala.scalanative.scriptbridge.ScriptObj
import scala.scalanative.unsafe.Tag.CFuncPtr1
import scalanative._
import unsafe._

object Main {
  def main(args: Array[String]): Unit = {
    val state = LuaState()
    state.openLibs()
    state.loadScalaUtils()
    state.registerModule(Foo)
//    state.doFile("hello.lua")
    state.doString(
      // language=Lua
      """
        |Foo = scala.load("Foo")
        |foo = Foo.new(1)
        |s = Foo.getBar()
        |Foo.print(s)
        """.stripMargin)
    state.free()
    println("DONE")
  }

}

@ScriptObj
@debug
class Foo(var i: Int) {
  def add(a: Int): Unit = i += a
  def get: Int = i
}

class Bar

object Foo extends LuaModule {
  def getBar: Bar = new Bar
  def print(bar: Bar): Unit = println(bar)
}
