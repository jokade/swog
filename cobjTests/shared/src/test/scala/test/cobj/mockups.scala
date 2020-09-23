package test.cobj

import scala.scalanative.cobj.{CObj, CObject, ResultPtr}
import scala.scalanative.unsafe.{CFuncPtr0, CFuncPtr1, CStruct1, Ptr, extern}

@CObj
object Callbacks {
  def exec0(f: CFuncPtr0[Int]): Int = extern
  def exec1(f: CFuncPtr1[Int,Int], i: Int): Int = extern
}

class NumberLike(var __ptr: Ptr[Byte]) extends CObject

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
