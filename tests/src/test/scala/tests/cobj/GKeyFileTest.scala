package tests.cobj

import utest._

import scala.scalanative.native._

object GKeyFileTest extends TestSuite {
  val tests = Tests {
    'setAndGet-{
      val keys = new GKeyFile
      keys.setInteger(c"foo",c"bar",42)
      assert( keys.getInteger(c"foo",c"bar",null) == 42 )
    }
  }

  @CObj
  class GKeyFile {
    def getInteger(group_name: CString, key: CString, error: Ptr[Byte]): Int = extern
    def setInteger(group_name: CString, key: CString, value: Int): Unit = extern
  }
}
