
package test.cobj

import scala.scalanative.cobj.{CObj, CObject, returnsThis}
import scala.scalanative.unsafe.{CInt, extern, name}

@CObj
class Number extends CObject {
  def getValue(): CInt = extern
  def setValue(value: CInt): Unit = extern
  def free(): Unit = extern
  @returnsThis
  def self(): this.type = extern
}

object Number {
  @name("number_new")
  def apply(): Number = extern
}