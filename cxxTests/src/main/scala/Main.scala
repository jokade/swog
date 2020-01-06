import de.surfice.smacrotools.debug

import scalanative._
import unsafe._
import scala.scalanative.annotation.InlineSource
import scala.scalanative.cobj.CObj
import scala.scalanative.cxx.{Cxx, CxxObject, ScalaCxx, ScalaCxxObject, constructor, cxxName, delete}
import scala.scalanative.runtime.{Intrinsics, RawPtr}
import scala.scalanative.unsafe.Tag.CFuncPtr1


object Main {
  def main(args: Array[String]): Unit = {
    val foo = SimpleScalaCxx()
    println(foo.test_getInt(1))
//    println(foo.test_getBool())
    foo.free()
  }
}

@ScalaCxx
@debug
abstract class AbstractClass extends ScalaCxxObject {
  def getBool(): Boolean

}

@ScalaCxx
@debug
class SimpleScalaCxx extends ScalaCxxObject {
  @delete
  def free(): Unit = extern

//  override def getBool(): CBool = true
//  @cxxName("getBool")
//  def test_getBool(): Boolean = extern

  def getInt(i: Int): Int = i+1
  @cxxName("getInt")
  def test_getInt(i: Int): Int = extern
}

object SimpleScalaCxx {
  @constructor
  def apply(): SimpleScalaCxx = extern
}
