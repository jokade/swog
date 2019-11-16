package scala.scalanative.cobj

import scala.scalanative.unsafe._

final class ResultPtr[T](val ptr: Ptr[T]) extends CObject {
  @inline def __ptr: Ptr[Byte] = ptr.asInstanceOf[Ptr[Byte]]
  @inline def isDefined: Boolean = ???
  @inline def isEmpty: Boolean = !isDefined
  @inline def value: T = ???
  @inline def wrappedValue(implicit wrapper: CObjectWrapper[T]): T = ???
}
object ResultPtr {
  def alloc[T](implicit zone: Zone): ResultPtr[T] = ???
  def stackalloc[T]: ResultPtr[T] = ???
}