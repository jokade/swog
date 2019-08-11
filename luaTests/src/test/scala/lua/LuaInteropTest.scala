package lua

import de.surfice.smacrotools.debug
import utest._

import scala.scalanative.scriptbridge.ScriptObj

object LuaInteropTest extends TestSuite {

  def withLua[T](script: String)(checks: Lua=>T): T = {
    val lua = Lua()
    lua.init()
    lua.registerModule(InteropTestMockup)
    lua.execString("EUT = scala.load('lua.InteropTestMockup')")
    lua.execString(script)
    val res = checks(lua)
    lua.free()
    res
  }

  val tests = Tests {
    'functions-{
      'testBoolean-{
        withLua("return EUT.testBoolean(false)") { lua =>
          lua.boolean(-1) ==> true
        }
      }
      'testInt-{
        withLua("return EUT.testInt(1)") { lua =>
          lua.int(-1) ==> 2
        }
      }
      'testLong-{
        withLua("return EUT.testLong(1)") { lua =>
          lua.long(-1) ==> Long.MaxValue-1
        }
      }
      'testFloat-{
        withLua("return EUT.testFloat(123.456)") { lua =>
          lua.float(-1) ==> 0F
        }
      }
      'testDouble-{
        withLua("return EUT.testDouble(1.0)") { lua =>
          lua.double(-1) ==> Double.MaxValue - 1.0
        }
      }
      'testString-{
        withLua("return EUT.testString('he')") { lua =>
          lua.string(-1) ==> "hello"
        }
      }
      'testOption-{
        'nil-{
          withLua("return EUT.testStringOption(nil)") { lua =>
            lua.stringOption(-1) ==> None
          }
        }
        'string-{
          withLua("return EUT.testStringOption('foo')") { lua =>
            lua.stringOption(-1) ==> Some("foo")
          }
        }
      }
      'testMap-{
        withLua(
          """
            |obj = {
            |  int = 1
            |}
            |return EUT.testMap(obj)
            |""".stripMargin) { lua =>
          val res = lua.table(-1).toMap()
//          res("int") ==> 42
//          res("bool") ==> true
//          res("sub") ==> Map("string"->"hello")
        }
      }
    }

    'userObject-{
      'new-{
        withLua(
          """eut = EUT.new(42)
            |return eut:i()
            |""".stripMargin){ lua =>
          lua.int(-1) ==> 42
        }
      }
      'passUserObjectAsArg-{
        withLua(
          // language=Lua
          """eut1 = EUT.new(42)
            |eut2 = EUT.new(1)
            |eut1:add(eut2)
            |return eut1:int()
            |""".stripMargin) { lua =>
          lua.int(-1) ==> 43
        }
      }
    }
  }

}

@ScriptObj
@debug
class InteropTestMockup(_i: Int) {
  val i: Int = _i
  var int: Int = _i
  def add(m: InteropTestMockup): InteropTestMockup = {
    int += m.int
    this
  }
}
object InteropTestMockup extends LuaModule {
  def testBoolean(b: Boolean): Boolean = !b
  def testInt(i: Int): Int = i+1
  def testLong(l: Long): Long = Long.MaxValue - l
  def testFloat(f: Float): Float = f - 123.456F
  def testDouble(d: Double): Double = Double.MaxValue - d
  def testString(s: String): String = s + "llo"

  def testStringOption(s: Option[Any]): Option[String] = s match {
    case Some(s: String) => Some(s)
    case None => None
  }

  def testMap(m: Map[String,Any]): Map[String,Any] = {
    m.updated("int",42)
  }
}

