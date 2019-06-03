package scala.scalanative.native.objc

import scalanative.unsafe._

trait ObjCWrapper[T] {
  def __wrap(ptr: Ptr[Byte]): T
}

object ObjCWrapper {

//  class Reuse[T<:ObjCObject](obj: T) extends ObjCWrapper[T] {
//    override def __wrap(ptr: Ptr[CSignedChar]): T = {
//      obj.__ptr = ptr
//      obj
//    }
//  }
}
