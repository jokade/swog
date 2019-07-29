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
    state.doString(
      // language=Lua
      """
        |Foo = scala.load("Foo")
        |print(Foo.bar(false))
        |""".stripMargin)
    state.free()
    println("DONE")
  }

}

@ScriptObj
@debug
object Foo extends LuaModule {
  def incr(i: Int): Int = i+1
  def add(a: Double, b: Double): Double = a+b
  def bar(b: Boolean): String = b.toString
}
