package scala.scalanative

import com.sun.jna.Pointer

import scala.scalanative.unsafe.Ptr

package object runtime {

  type RawPtr = Long

//  def fromRawPtr[T](rawptr: RawPtr): Ptr[T] = ???

//  object Intrinsics {
//    def castObjectToRawPtr(o: Object): RawPtr = ??? //o.asInstanceOf[RawPtr]
//    def castRawPtrToObject(rawptr: RawPtr): Object = ??? //rawptr.asInstanceOf[]
//  }
}
