import de.surfice.smacrotools.debug

import scalanative._
import unsafe._
import objc._
import scala.scalanative.runtime.RawPtr

object Main {
  def main(args: Array[String]): Unit = {
    val i = Number.alloc()
    println(i.get())
    println("done")
  }
}

@ObjC
@debug
class Number extends ObjCObject {
  def get(): Int = extern

//  def init(i: Init): Int = extern
}

@ObjCClass
@debug
abstract class NumberClass extends ObjCClassObject {
  def alloc(): Number = extern
}

object Number extends NumberClass {
  override type InstanceType = Number
}