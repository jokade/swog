import de.surfice.smacrotools.debug
import lua.{LuaModule, LuaReg, LuaState, nolua}

import scala.scalanative.runtime.Intrinsics
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
        |print(foo)
        """.stripMargin)
    state.free()
    println("DONE")
  }

}

@ScriptObj
@debug
class Foo(var i: Int) {
  @nolua
  def add(): Unit = i += 1
  def add(a: Int): Unit = i += a
  def get: Int = i
}

class Bar

object Foo extends LuaModule {
  private lazy val bar = new Bar
  def getBar: Bar = bar
  def print(bar: Bar): Unit = println(bar)

}
