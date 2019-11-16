package scala.scalanative.unsafe

trait Zone {
  def alloc(size : scala.scalanative.unsafe.CSize) : scala.scalanative.unsafe.Ptr[scala.Byte]
  def close() : scala.Unit
  def isOpen : scala.Boolean
  def isClosed : scala.Boolean
}
object Zone {
}
