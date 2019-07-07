package scala.scalanative.interop

import scala.scalanative.unsafe.Ptr

trait ExternalObject {
  def __ptr: Ptr[Byte]
}
