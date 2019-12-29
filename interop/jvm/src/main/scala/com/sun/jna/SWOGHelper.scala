package com.sun.jna

import scala.scalanative.unsafe.{CString, Ptr}

object SWOGHelper {
  def nativeString(s: String): CString = new Ptr(new NativeString(s).getPointer)
}
