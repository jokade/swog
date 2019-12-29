package scala.scalanative

import scala.scalanative.unsafe.{Ptr, Tag}

package object interop {

  implicit final class RichPtr[T](val p: Ptr[T]) extends AnyVal {
    @inline def :=(value: T)(implicit tag: Tag[T]): Unit = !p = value
  }
}
