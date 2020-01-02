package scala.scalanative.cobj

import scala.scalanative.interop.ExternalObject
import scala.scalanative.unsafe.Ptr

/**
 * Base trait for all external objects represented by a Scala class annotated with @CObj
 */
trait CObject extends ExternalObject {
  //override def equals(obj: Any): Boolean = super.equals(obj)
  /// Wraps the underlying pointer into the specified type
  def as[T<:CObject](implicit wrapper: CObjectWrapper[T]): T = wrapper.wrap(__ptr)
}

trait MutableCObject extends CObject {
  def __ptr_=(ptr: Ptr[Byte]): Unit
}
