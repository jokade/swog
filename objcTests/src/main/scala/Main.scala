

import de.surfice.smacrotools.debug

import scalanative.native._
import objc._
import scala.scalanative.native.objc.runtime.ObjCObject

object Main {

  def main(args: Array[String]): Unit = Zone { implicit z =>
    val foo = Foo.alloc()
  }

}

//@ObjC
//@debug
//class Foo extends ObjCObject
//
//@ObjC
//@debug
//class Bar extends Foo {
//  def foo(): Unit = extern
//
//}

@ObjC
class NSObject extends ObjCObject

@ScalaObjC
@debug
class Foo(self: ObjCObject) extends NSObject {
  def foo(): Unit = {

  }
}

object Foo extends ObjCObject {
  def alloc(): Foo = extern
}
