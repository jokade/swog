package scala.scalanative

import java.util

import com.sun.jna.{FromNativeContext, Library, Native, Pointer, Structure}

import scala.scalanative.interop.jvm
import scala.scalanative.unsafe.{CString, CStruct1, CStruct2, Ptr}

trait Mockups extends Library {
  def ptest_return_string(s: CString): CString
  def ptest_struct_get(): Ptr[CStruct1[Int]]
}



object Mockups {

  private lazy val _inst: Mockups = Native.load("platformtest",classOf[Mockups])

  def returnString(s: CString): CString = _inst.ptest_return_string(s)

  def getStruct(): Ptr[CStruct1[Int]] = _inst.ptest_struct_get()
}
