package scala.scalanative

import scala.scalanative.runtime.{Intrinsics, RawPtr}
import scala.scalanative.unsafe.{Ptr, Tag}

package object interop {

  @inline def ptrFromRawPtr[T](p: RawPtr): Ptr[T] = runtime.fromRawPtr(p)
  @inline def objectToRawPtr(o: Object): RawPtr = Intrinsics.castObjectToRawPtr(o)
  @inline def rawPtrToObject(p: RawPtr): Object = Intrinsics.castRawPtrToObject(p)

  implicit final class RichPtr[T](val p: Ptr[T]) extends AnyVal {
    @inline def :=(value: T)(implicit tag: Tag[T]): Unit = !p = value
  }

}
