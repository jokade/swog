package scala.scalanative.native.objc

import scalanative.native._

trait ObjCWrapper[T] {
  def __wrap(ptr: Ptr[Byte]): T
}
