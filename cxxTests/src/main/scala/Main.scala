import de.surfice.smacrotools.debug

import scalanative._
import unsafe._
import scala.scalanative.annotation.InlineSource
import scala.scalanative.cobj.CObj
import scala.scalanative.cxx.{Cxx, constructor}


object Main {
  def main(args: Array[String]): Unit = {
//    val foo = Foo()
//    foo.bar()
  }
}
/*
@Cxx
@debug
class Bar {

}


@Cxx
@debug
class Foo {
  def bar(): Bar = extern
}

object Foo {
  @constructor
  def apply(): Foo = extern

  def doSomethind(f: Boolean): Int = extern

}
*/
