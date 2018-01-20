import de.surfice.smacrotools.debug

import scala.scalanative.native.CObj.{CObjWrapper, CRef, Out, returnsThis}
import scalanative.native._
import CObj.implicits._

object Main {

  def main(args: Array[String]): Unit = Zone{ implicit z: Zone =>

  }

}

@CObj
class Foo

@CObj
@debug
class Bar extends Foo with CObjWrapper {
  @returnsThis
  def foo()(out: Out[Int]): Bar = extern
}

object Bar {
  def xx(in: Int)(out: Out[Int]): Int = extern
}

//@CObj
//abstract class X

//@CObj
//class Y extends X
