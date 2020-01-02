package scala.scalanative.cobj

import scala.scalanative.unsafe._

trait CObjectWrapper[T] {
  def wrap(ptr: Ptr[Byte]): T
  def unwrap(value: T): Ptr[Byte]
}

trait CObjectWrapper1[T,I1] extends CObjectWrapper[T] {
  final override def wrap(ptr: Ptr[Byte]): T = ???
  def wrap(ptr: Ptr[Byte])(implicit arg1: I1): T
  def unwrap(value: T): Ptr[Byte]
}

trait CObjectWrapper2[T,I1,I2] extends CObjectWrapper[T] {
  final override def wrap(ptr: Ptr[Byte]): T = ???
  def wrap(ptr: Ptr[Byte])(implicit arg1: I1, arg2: I2): T
  def unwrap(value: T): Ptr[Byte]
}

trait CObjectWrapper3[T,I1,I2,I3] extends CObjectWrapper[T] {
  final override def wrap(ptr: Ptr[Byte]): T = ???
  def wrap(ptr: Ptr[Byte])(implicit arg1: I1, arg2: I2, arg3: I3): T
  def unwrap(value: T): Ptr[Byte]
}

trait CObjectWrapper4[T,I1,I2,I3,I4] extends CObjectWrapper[T] {
  final override def wrap(ptr: Ptr[Byte]): T = ???
  def wrap(ptr: Ptr[Byte])(implicit arg1: I1, arg2: I2, arg3: I3, arg4: I4): T
  def unwrap(value: T): Ptr[Byte]
}

object CObjectWrapper {

  class SingletonFactory[T<:MutableCObject](factory: CObjectWrapper[T]) extends CObjectWrapper[T] {
    private lazy val _instance: T = factory.wrap(null)

    override def wrap(ptr: Ptr[Byte]): T = {
      _instance.__ptr = ptr
      _instance
    }

    override def unwrap(value: T) = ???
  }

  implicit object CStringWrapper extends CObjectWrapper[CString] {
    override def wrap(ptr: Ptr[Byte]): CString = ptr //ptrToCString(ptr)
    override def unwrap(value: CString): Ptr[Byte] = value.asInstanceOf[Ptr[Byte]]
  }

  implicit object IntWrapper extends CObjectWrapper[Int] {
    override def wrap(ptr: Ptr[Byte]): Int = ptr.toInt
    override def unwrap(value: Int): Ptr[Byte] = ???
  }

  implicit object NullWrapper extends CObjectWrapper[Null] {
    override def wrap(ptr: Ptr[Byte]): Null = null
    override def unwrap(value: Null) = null
  }

  object Implicits {
    implicit object StringWrapper extends CObjectWrapper[String] {
      override def wrap(ptr: Ptr[Byte]): String = fromCString(ptr)
      override def unwrap(value: String): Ptr[Byte] = throw new RuntimeException("CObjWrapper.StringWrapper: unwrapping a Scala String to a CString is not supported")
    }
  }

}
