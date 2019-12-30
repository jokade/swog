package com.sun.jna

import scala.scalanative.unsafe.{CString, Ptr}

object SWOGHelper {
  def nativeString(s: String): CString = Ptr(new NativeString(s).getPointer.peer)
  def toPtr[T](p: Pointer): Ptr[T] = Ptr(p.peer)

  def setPointer(p: Pointer, offset: Long, value: Pointer): Unit = Native.setPointer(p,p.peer,offset,if(value==null) 0 else value.peer)

}
