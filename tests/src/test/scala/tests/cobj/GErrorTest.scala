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


}
