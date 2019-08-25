import de.surfice.smacrotools.debug

import scalanative._
import unsafe._
import scala.scalanative.annotation.InlineSource
import scala.scalanative.cobj.CObj
import scala.scalanative.cxx.{Cxx, ScalaCxx, constructor, cxxName, delete}
import scala.scalanative.runtime.{Intrinsics, RawPtr}
import scala.scalanative.unsafe.Tag.CFuncPtr1


object Main {
  def main(args: Array[String]): Unit = {
    val foo = SimpleScalaCxx()
    println(foo.test_getInt(1))
  }
}

@ScalaCxx
@debug
class SimpleScalaCxx {
  @delete
  def free(): Unit = extern

  def getInt(i: Int): Int = i
  @cxxName("getInt")
  def test_getInt(i: Int): Int = extern
}
object SimpleScalaCxx {
  @constructor
  def apply(): SimpleScalaCxx = extern
}

