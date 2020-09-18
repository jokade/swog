package scala.scalanative.interop

import scala.scalanative.unsafe.Ptr

trait ExternalObject {
  def __ptr: Ptr[Byte]
  protected def __ptr_=(p: Ptr[Byte]): Unit
}



