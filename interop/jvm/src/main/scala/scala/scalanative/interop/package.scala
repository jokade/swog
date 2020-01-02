package scala.scalanative

import scala.scalanative.runtime.RawPtr
import scala.scalanative.unsafe.Ptr

package object interop {
  @inline def ptrFromRawPtr[T](p: RawPtr): Ptr[T] = Ptr(p)
  @inline def objectToRawPtr(o: Object): RawPtr = ???
  @inline def rawPtrToObject(p: RawPtr): Object = ???
}
