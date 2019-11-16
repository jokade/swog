package scala.scalanative.cobj

import scala.scalanative.unsafe.Ptr

trait ResultValue[T<:CObject] extends CObject {
  def __ptr: Ptr[Byte]
  @inline def wrappedValue(implicit wrapper: CObjectWrapper[T]): T = ???
  @inline final def :=[R](f: (ResultValue[T]) => R): R = f(this)
}
