package tests.cobj

import de.surfice.smacrotools.debug

import scalanative.cobj._
import scalanative.unsafe._

@CObj
class Number {
  def getValue(): CInt = extern
  def setValue(value: CInt): Unit = extern
  def free(): Unit = extern
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
@debug
class SList[T<:CObject] {
  def isEmpty: Boolean = extern
  def size: Int = extern
  def prepend(value: T): SList[T] = extern

  // returns null if the specified index does not exist
  @nullable
  def itemAt(index: Int)(implicit wrapper:CObjectWrapper[T]): T = extern

}

object SList {
  @name("slist_new")
  def apply[T<:CObject](): SList[T] = extern
}
