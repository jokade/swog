package tests.cobj

import de.surfice.smacrotools.debug

import scalanative.cobj._
import scalanative.unsafe._

@CObj
@debug
class Number {
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


@CObj
class Counter extends Number {
  def increment(): CInt = extern
}

object Counter {
  @name("counter_new")
  def apply(): Counter = extern
  def withStepSize(stepSize: CInt): Counter = extern
}

@CObj(prefix = "slist_")
class SList[T] {
  def isEmpty: Boolean = extern
  def size: Int = extern
  def prepend(value: T)(implicit wrapper: CObjectWrapper[T]): SList[T] = extern

  // returns null if the specified index does not exist
  @nullable
  def itemAt(index: Int)(implicit wrapper:CObjectWrapper[T]): T = extern

}

object SList {
  @name("slist_new")
  def apply[T<:CObject](): SList[T] = extern
}

@CObj
object Callbacks {
  def exec0(f: CFuncPtr0[Int]): Int = extern
}
