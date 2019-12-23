package com.sun.jna

import scala.scalanative.unsafe.{CString, Ptr, Zone}

object SWOGHelper {
  def nativeString(s: String): CString = new Ptr(new NativeString(s).getPointer)
}
