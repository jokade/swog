package scala.scalanative.cobj

import scala.scalanative.unsafe.Ptr

final class ResultPtr[T](val ptr: Ptr[T]) extends CObject {
  @inline def __ptr: Ptr[Byte] = ptr.asInstanceOf[Ptr[Byte]]
  @inline def isDefined: Boolean = ??? //!ptr != null
  @inline def isEmpty: Boolean = !isDefined

  @inline def value: T = ???
  @inline def wrappedValue(implicit wrapper: CObjectWrapper[T]): T = ???
}
