import de.surfice.smacrotools.debug

import scalanative.native._
import cobj._

object Main {

  def main(args: Array[String]): Unit = Zone{ implicit z: Zone =>

  }

}

@CObj.Mutable
@debug
class Foo[T](var __ref: Ref[Byte]) extends CObjWrapper {
//  @CObj.updatesThis
  @nullable
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
