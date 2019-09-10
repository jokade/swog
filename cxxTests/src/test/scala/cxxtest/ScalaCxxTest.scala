package cxxtest

import de.surfice.smacrotools.debug
import utest._

import scalanative._
import unsafe._
import cxx._
import scala.scalanative.runtime.{Boxes, CFuncRawPtr, Intrinsics}

object ScalaCxxTest extends TestSuite {
  val tests = Tests {
    'SimpleScalaCxx-{
      val eut = SimpleScalaCxx()
      println(eut.test_getInt(1))
      eut.free()

    }
  }

}

@ScalaCxx
@debug
class SimpleScalaCxx {
  @delete
  def free(): Unit = extern

  def getInt(i: Int): Int = 42
  @cxxName("getInt")
  def test_getInt(i: Int): Int = extern
}
object SimpleScalaCxx {
  @constructor
  def apply(): SimpleScalaCxx = extern
}

