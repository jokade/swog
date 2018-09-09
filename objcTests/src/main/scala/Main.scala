

import de.surfice.smacrotools.debug

import scalanative.native._
import objc._
import scala.scalanative.native.objc.runtime.ObjCObject

object Main {

  def main(args: Array[String]): Unit = Zone { implicit z =>

  }

}

@ScalaObjC
@debug
class Foo(self: ObjCObject) {
  var foo: ObjCObject = _

  def bar: ObjCObject = ???
}