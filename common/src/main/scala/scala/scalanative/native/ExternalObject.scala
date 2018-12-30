package scala.scalanative.native

trait ExternalObject {
  def __ptr: Ptr[Byte]
}
