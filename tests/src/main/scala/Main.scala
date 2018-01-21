import de.surfice.smacrotools.debug

import scala.scalanative.native.CObj._
import scalanative.native._
import CObj.implicits._

object Main {

  def main(args: Array[String]): Unit = Zone{ implicit z: Zone =>

  }

}

@CObj.Mutable
@debug
class Foo(var __ref: Ref[Byte]) extends CObjWrapper {
  @CObj.updatesThis
  def foo(): Foo = extern
}

//@CObj
//class Bar extends Foo with CObjWrapper {
//  @returnsThis
//  def foo()(out: Out[Int]): Bar = extern
//}

//object Bar {
//  def create(): Bar = extern
//}

//@CObj
//abstract class X

//@CObj
//class Y extends X
