package scala.scalanative.interop

import scala.scalanative.unsafe.{CString, Ptr}

package object jvm {

  @inline final def ptrToCString(p: Ptr[Byte]): CString = p
}
