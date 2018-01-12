package tests.cobj

import de.surfice.smacrotools.debug
import utest._

import scala.scalanative.native.CObj.Out
import scala.scalanative.native._

object GKeyFileTest extends TestSuite {
  val tests = Tests {
    'setAndGet-{
      val keys = new GKeyFile
      keys.setInteger(c"foo",c"bar",42)
    }
    'getError-Zone{ implicit z: Zone =>
      implicit val err = Out.alloc[GError]
      val keys = new GKeyFile
      keys.setInteger(c"group",c"key",42)

      keys.getInteger(c"group",c"key")
      assert(err.isEmpty)

      keys.getInteger(c"foo",c"bar")
      assert(
        err.isDefined,
        err.value.get.code == 4
      )
      err.value.get.free()

      err.clear()
      keys.getInteger(c"group",c"key")
      assert(err.isEmpty)
    }
  }

  @CObj
  class GKeyFile {
    def getInteger(group_name: CString, key: CString)(implicit error: Out[GError]): Int = extern
    def setInteger(group_name: CString, key: CString, value: Int): Unit = extern
  }
}
