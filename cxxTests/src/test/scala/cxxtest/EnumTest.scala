package cxxtest

import de.surfice.smacrotools.debug

import scalanative._
import unsafe._
import cxx._
import utest._

import scala.scalanative.annotation.InlineSource

object EnumTest extends TestSuite {
  mockups.loadCxx()

  val tests = Tests {
    'Int-{
      val eut = TestEnum(FooEnum.Bar)
      eut.get ==> FooEnum.Bar
    }
  }


  @Cxx
  class TestEnum extends CxxObject {
    def get: FooEnum.Value = extern
  }
  object TestEnum {
    @constructor
    def apply(en: FooEnum.Value): TestEnum = extern
  }
}


