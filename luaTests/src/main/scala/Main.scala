import de.surfice.smacrotools.debug
import lua._

import scala.scalanative.runtime.{Intrinsics, RawPtr}
import scala.scalanative.scriptbridge.ScriptObj
import scala.scalanative.unsafe.Tag.CFuncPtr1
import scalanative._
import unsafe._

object Main {
  def main(args: Array[String]): Unit = {
    val lua = Lua()
    lua.init()
    lua.registerModule(Foo)
    lua.execFile("hello.lua")
    /*
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
        |obj = {
        |  foo = 42
        |}
        |map = Foo.withMap(obj)
        |print(map.bar)
        |
        |Foo.withOption(-1)
        |
        |print(Foo.withOption(41))
        |print(Foo.withOption(nil))
        |print(Foo.withOption("foo"))
        |""".stripMargin
    )

     */
    lua.free()
    println("DONE")
  }

}

@ScriptObj
@debug
class Foo(_num: Int) {
  val i: Int = _num
  var num: Int = _num
  def incr(): Unit = num+=1
  def add(other: Foo): Unit = num += other.num
}

object Foo extends LuaModule {
  def withTable(obj: LuaTable): Unit = {
    obj.get("foo") match {
      case Some(l: Long) => println(l)
    }
  }

  def withMap(obj: Map[String,Any]): Map[String,Any] = {
    obj.updated("bar",43)
  }

  def withOption(obj: Option[Any]): Option[Long] = obj match {
    case Some(i: Long) => Some(i+1)
    case _ => None
  }
}
