import de.surfice.smacrotools.debug
import lua._

import scala.scalanative.runtime.Intrinsics
import scala.scalanative.scriptbridge.ScriptObj
import scala.scalanative.unsafe.Tag.CFuncPtr1
import scalanative._
import unsafe._

object Main {
  def main(args: Array[String]): Unit = {
    val lua = Lua()
    lua.init()
//    lua.registerModule(Foo)
//    lua.execFile("hello.lua")
    lua.execString(
      // language=Lua
      """config = {
        |  foo = 42,
        |  bar = {
        |    string = "Hello world!"
        |  }
        |}
        |return config
       """.stripMargin)
    lua.getGlobalValue("config") match {
      case Some(obj: LuaTable) =>
        println(obj.toMap())
    }

    lua.registerModule(Foo)
    lua.execString(
      // language=Lua
      """Foo = scala.load("Foo")
        |-- create new instance
        |foo = Foo.new(42)
        |-- print current value of num
        |print(foo:num())
        |-- call Scala method incr()
        |foo:incr()
        |print(foo:num())
        |-- set num = -1
        |foo:setNum(-1)
        |print(foo:num())
        |""".stripMargin
    )
    lua.free()
//    state.free()
    println("DONE")
  }

}

@ScriptObj
@debug
class Foo(_num: Int) {
  var num: Int = _num
  @luaname()
  def incr(): Unit = num+=1
}

object Foo extends LuaModule {

}
