package cxxtest

import de.surfice.smacrotools.debug
import utest._

import scalanative._
import unsafe._
import cxx._

object ScalaCxxTest extends TestSuite {
  val tests = Tests {
    'create-{
    }
  }

  @ScalaCxx
  @debug
  class EUT {

  }
  object EUT {
    @constructor
    def apply(): EUT = extern
  }
}


