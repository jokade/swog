package test.cobj

import de.surfice.smacrotools.debug

import scala.scalanative.cobj.{ResultPtr, _}
import scala.scalanative.unsafe._

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


@CObj
@debug
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
class SList[T] {

  def isEmpty: Boolean = extern
  def size: Int = extern
  def prepend(value: T)(implicit wrapper: CObjectWrapper[T]): SList[T] = extern

  // returns null if the specified index does not exist
  @nullable
  def itemAt(index: Int)(implicit wrapper: CObjectWrapper[T]): T = extern

}

object SList {
  @name("slist_new")
  def apply[T<:CObject](): SList[T] = extern
}


@CObj
object Callbacks {
  def exec0(f: CFuncPtr0[Int]): Int = extern
  def exec1(f: CFuncPtr1[Int,Int], i: Int): Int = extern
}

class NumberLike(val __ptr: Ptr[Byte]) extends CObject

@CObj
object ImplicitArgs {
  type OutStruct = CStruct1[Int]

  def int()(implicit out: ResultPtr[Int]): Unit = extern
  def long()(implicit out: ResultPtr[Long]): Unit = extern
  def double()(implicit out: ResultPtr[Double]): Unit = extern
  def struct()(implicit out: ResultPtr[OutStruct]): Unit = extern
  def number()(implicit out: ResultPtr[Number]): Unit = extern
  def multiArgs()(implicit num1: Number, num2: NumberLike): Int = extern
}


