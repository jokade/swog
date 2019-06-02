package scala.scalanative.cobj

import scala.scalanative.unsafe._

trait CObjectWrapper[+T] {
  def wrap(ptr: Ptr[Byte]): T
//  def unwrap[S>:T](value: S): Ptr[Byte]
}

object CObjectWrapper {

  class SingletonFactory[T<:MutableCObject](factory: CObjectWrapper[T]) extends CObjectWrapper[T] {
    private lazy val _instance: T = factory.wrap(null)

    override def wrap(ptr: Ptr[Byte]): T = {
      _instance.__ptr = ptr
      _instance
    }
  }

  implicit object CStringWrapper extends CObjectWrapper[CString] {
    override def wrap(ptr: Ptr[CSignedChar]): CString = ptr
//    override def unwrap[S>:CString](value: S): Ptr[Byte] = value.asInstanceOf[Ptr[Byte]]
  }

  object Implicits {
    implicit object StringWrapper extends CObjectWrapper[String] {
      override def wrap(ptr: Ptr[Byte]): String = fromCString(ptr)
//      override def unwrap[S>:T(value: String): Ptr[Byte] = throw new RuntimeException("CObjWrapper.StringWrapper: unwrapping a Scala String to a CString is not supported")
    }

  }

}
