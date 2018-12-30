package scala.scalanative.native.cobj

import scala.scalanative.native.Ptr

trait CObjWrapper[T] {
  def wrap(ptr: Ptr[Byte]): T
  def unwrap(value: T): Ptr[Byte]
}
