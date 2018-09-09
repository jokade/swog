package scala.scalanative.native.cobj

import scalanative.native._

object Implicits {

  implicit class RichRef[T](val ref: Ref[T]) extends AnyVal {
    @inline def toPtr: Ptr[T] = ref.cast[Ptr[T]]
  }
}
