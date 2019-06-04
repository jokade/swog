package scala.scalanative.cobj

import scala.scalanative.interop.ExternalObject
import scala.scalanative.unsafe.Ptr

/**
 * Base trait for all external objects represented by a Scala class annotated with @CObj
 */
trait CObject extends ExternalObject {
  override def equals(obj: Any): Boolean = super.equals(obj)
}

trait MutableCObject extends CObject {
  def __ptr_=(ptr: Ptr[Byte]): Unit
}
