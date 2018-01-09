package tests.cobj

import utest._
import scalanative.native._

object GErrorTest extends TestSuite {
  val tests = Tests {
    'new-{
      val error = new GError(1,42,c"foo")
      assert(
        error.domain == 1,
        error.code == 42,
        fromCString(error.msg) == "foo"
      )
    }
  }

  @CObj
  class GError(_domain: Int, _code: Int, _msg: CString)
    extends CObj.CRef[CStruct3[Int,Int,CString]] {
    def domain: Int = !__ref._1
    def code: Int = !__ref._2
    def msg: CString = !__ref._3
  }
}
