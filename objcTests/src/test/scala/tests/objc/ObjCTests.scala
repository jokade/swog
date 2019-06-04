package tests.objc

import utest._
import de.surfice.smacrotools.debug

object ObjCTests extends TestSuite {

  val tests = Tests{
    'a-{

      val n = Number.alloc().init(42)
      println("yeah")
    }
  }

}


