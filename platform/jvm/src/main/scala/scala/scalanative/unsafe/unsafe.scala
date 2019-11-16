package scala.scalanative

import com.sun.jna.Pointer

import scala.annotation.StaticAnnotation

package object unsafe {
  // TODO: check type mappings and correct
  type CBool             = Boolean
  type CChar             = Char
  type CSignedChar       = Char
  type CUnsignedChar     = Char
  type CShort            = Short
  type CUnsignedShort    = Short
  type CInt              = Int
  type CLongInt          = Int
  type CUnsignedInt      = Int
  type CUnsignedLongInt  = Int
  type CLong             = Long
  type CUnsignedLong     = Long
  type CLongLong         = Long
  type CUnsignedLongLong = Long
  type CSize             = Long
  type CPtrDiff          = Long
  type CWideChar         = Char
  type CChar16           = Short
  type CChar32           = Int
  type CFloat            = Float
  type CDouble           = Double
  type CString           = String
  
  def extern: Nothing = ???
  
  type Ptr[T] = Pointer
  implicit final class RichPointer[T](val p: Ptr[T]) extends AnyVal {
    def unary_!(): T = ??? 
  }
  
  def fromCString(cstr: CString): String = ???
  def toCString(s: String)(implicit zone: Zone): CString = ???

  final class name(name : String) extends StaticAnnotation
}
