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
class Foo[T](var __ref: Ref[Byte]) extends CObjWrapper {
//  @CObj.updatesThis
  @CObj.nullable
  def foo(): Foo[T] = extern
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
