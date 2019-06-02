package scala.scalanative.cobj

import scala.scalanative.unsafe._

trait CObjWrapper[T] {
  def wrap(ptr: Ptr[Byte]): T
  def unwrap(value: T): Ptr[Byte]
}

object CObjWrapper {
  implicit object CStringWrapper extends CObjWrapper[CString] {
    override def wrap(ptr: Ptr[CSignedChar]): CString = ptr
    override def unwrap(value: CString): Ptr[Byte] = value
  }

  object Implicits {
    implicit object StringWrapper extends CObjWrapper[String] {
      override def wrap(ptr: Ptr[Byte]): String = fromCString(ptr)
      override def unwrap(value: String): Ptr[Byte] = throw new RuntimeException("CObjWrapper.StringWrapper: unwrapping a Scala String to a CString is not supported")
    }

  }

}
