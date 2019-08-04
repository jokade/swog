import de.surfice.smacrotools.debug
import lua.{Lua, LuaModule, LuaReg, LuaState, LuaTable, nolua}

import scala.scalanative.runtime.Intrinsics
import scala.scalanative.scriptbridge.ScriptObj
import scala.scalanative.unsafe.Tag.CFuncPtr1
import scalanative._
import unsafe._

object Main {
  def main(args: Array[String]): Unit = {
    val lua = Lua()
    lua.init()
    lua.registerModule(Foo)
//    lua.execFile("hello.lua")
    lua.execString(
      // language=Lua
      """Foo = scala.load('Foo')
        |a = {
        |  b = {
        |    i = 1
        |  }
        |}
        |a['foo'] = 44
        |a['bar'] = 'hello world'
        |Foo.print(a)
        """.stripMargin)
    lua.free()
//    state.free()
    println("DONE")
  }

}

@ScriptObj
@debug
class Foo(var i: Int) {

}

class Bar

object Foo extends LuaModule {
  def print(table: LuaTable): Unit = {
    println(table.get("foo"))
    println(table.get("bar"))
    val b = table("b").asInstanceOf[LuaTable]
    println(table.get("foo"))
    println(b.get("i"))
  }
}
