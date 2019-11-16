package scala.scalanative.cobj

import scala.scalanative.unsafe._

final class ResultPtr[T](val ptr: Ptr[T]) extends CObject {
  @inline def __ptr: Ptr[Byte] = ptr.asInstanceOf[Ptr[Byte]]
  @inline def isDefined: Boolean = !ptr != null
  @inline def isEmpty: Boolean = !isDefined
  //  @inline def valuePtr: Ptr[Byte] = !ptr
  @inline def value: T = ???
  @inline def wrappedValue(implicit wrapper: CObjectWrapper[T]): T = ???
  //  @inline def value_=(v: T): Unit = macro Result.Macros.setValueImpl[T]
  //  @inline def option: Option[T] = macro Result.Macros.optionImpl[T]
  //  @inline def clear(): Unit = !ptr = null
}
