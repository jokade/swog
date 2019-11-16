package scala.scalanative


import com.sun.jna.{Library, Native, Pointer}

import scala.scalanative.unsafe.{CSize, CString}

trait LibC extends Library {
  def strlen(s: CString): Long
  def printf(s: CString): Unit
}
object LibC {
  private lazy val _inst = Native.load("c",classOf[LibC])

  def strlen(s: CString): CSize = _inst.strlen(s)
  def printf(s: CString): Unit = _inst.printf(s)
}
