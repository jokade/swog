package scala.scalanative.interop

import scala.scalanative.unsafe.{CString, Ptr}

object Platform {
  @inline final def ptrToCString(p: Ptr[Byte]): CString = p.asInstanceOf[CString]
}
