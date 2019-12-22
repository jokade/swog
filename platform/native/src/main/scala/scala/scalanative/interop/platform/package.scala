package scala.scalanative.interop

import scala.scalanative.unsafe.{CString, Ptr}

package object platform {

  @inline final def ptrToCString(p: Ptr[Byte]): CString = p
}
