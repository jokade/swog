package cxxtest

import de.surfice.smacrotools.debug
import utest._

import scalanative._
import unsafe._
import cxx._
import scala.scalanative.annotation.InlineSource

object ScalaCxxTest extends TestSuite {
  val tests = Tests {
    'usingFromCxx-{
      FromNative.create()
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

  @InlineSource("Cxx",
    """extern "C" {
         using namespace cxxtest::ScalaCxxTest;
         EUT* cxxtest_create() { return new EUT; }
       }""")
  @extern
  object FromNative {
    @name("cxxtest_create")
    def create(): EUT = extern
  }
}




