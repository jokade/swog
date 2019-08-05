package lua

import utest._

object LuaStateTest extends TestSuite {
  val state = LuaState()
  val tests = Tests {
    'StackOps-{
      'boolean-{
        state.pushBoolean(true)
        state.getBoolean(-1) ==> true
        state.isBoolean(-1) ==> true
      }
      'integer-{
        state.pushInteger(Long.MaxValue)
        state.pushInteger(Long.MinValue)
        state.getInteger(-2)==> Long.MaxValue
        state.getInteger(-1)==> Long.MinValue
        state.isInteger(-2) ==> true
      }
      'number-{
        state.pushNumber(Double.MaxValue)
        state.pushNumber(Double.MinPositiveValue)
        state.getNumber(-2) ==> Double.MaxValue
        state.getNumber(-1) ==> Double.MinPositiveValue
        state.isNumber(-2) ==> true
      }
      'string-{
        state.pushString("hello")
        state.getString(-1) ==> "hello"
        state.isString(-1) ==> true
      }
      'nil-{
        state.pushNil()
        state.isNil(-1) ==> true
      }
      'cfunction- {
        state.pushCFunction(null)
        state.isCFunction(-1) ==> true
      }
    }

    'WriteRead-{
      state.writeGlobal("int",42)
      state.writeGlobal("real",123.456)
      state.writeGlobal("string","hello")

      state.readGlobalInteger("int") ==> Some(42)
      state.readGlobalInteger("real") ==> None
      state.readGlobalInteger("string") ==> None
      state.readGlobalInteger("foo") ==> None

      state.readGlobalNumber("real") ==> Some(123.456)
      state.readGlobalNumber("int") ==> Some(42.0)
      state.readGlobalNumber("string") ==> None
      state.readGlobalNumber("foo") ==> None

      state.readGlobalString("string") ==> Some("hello")
      state.readGlobalString("int") ==> Some("42")
      state.readGlobalString("foo") ==> None
    }
  }
}
