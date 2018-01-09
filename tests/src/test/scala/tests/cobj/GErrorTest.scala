package tests.cobj

import de.surfice.smacrotools.debug
import utest._

import scalanative.native._

object GErrorTest extends TestSuite {
  val tests = Tests {
    'new-{
//      val error = new GError(1,42,c"foo %s",c"bar")
      val error = new GError(1,42,c"foo")
      assert(
        error.domain == 1,
        error.code == 42,
        fromCString(error.msg) == "foo"
      )
    }

  }

  @CObj
  // TODO: add args: CVararg*
  class GError(_domain: Int, _code: Int, _fmt: CString, args: CVararg*)
    extends CObj.CRef[CStruct3[Int,Int,CString]] {
    def domain: Int = !__ref._1
    def code: Int = !__ref._2
    def msg: CString = !__ref._3
  }
}
