package cxxtest

import de.surfice.smacrotools.debug

import scalanative._
import unsafe._
import cxx._
import utest._

import scala.scalanative.annotation.ExternalSource

object EnumTest extends TestSuite {
  mockups.loadCxx()

  val tests = Tests {
    'Int-{
      val eut = TestEnum(FooEnum.Bar)
      eut.get ==> FooEnum.Bar
    }
  }

  object FooEnum extends CxxEnum {
    val Foo = Value(1)
    val Bar = Value(2)
  }

  @Cxx
  @debug
  class TestEnum {
    def get: FooEnum.Value = extern
  }
  object TestEnum {
    @constructor
    def apply(en: FooEnum.Value): TestEnum = extern
  }
}


