package scala.scalanative

import com.sun.jna.{Callback, Pointer, SWOGHelper}

import scala.annotation.StaticAnnotation
import scala.scalanative.unsigned.{UInt, ULong}

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
  type CUnsignedInt      = UInt
  type CUnsignedLongInt  = UInt
  type CLong             = Long
  type CUnsignedLong     = ULong
  type CLongLong         = Long
  type CUnsignedLongLong = ULong
  type CSize             = Long
  type CSSize            = Long
  type CPtrDiff          = Long
  type CWideChar         = Char
  type CChar16           = Short
  type CChar32           = Int
  type CFloat            = Float
  type CDouble           = Double
  type CString           = Ptr[Byte]

  type CFuncPtr0[R]               = Callback
  type CFuncPtr1[T1,R]            = Callback
  type CFuncPtr2[T1,T2,R]         = Callback
  type CFuncPtr3[T1,T2,T3,R]      = Callback


  final def extern: Nothing = throw new UnsupportedOperationException("A call to 'extern' was not properly transformed.")

  /** Stack allocate a value of given type.
   *
   *  Note: unlike alloc, the memory is not zero-initialized.
   */
  def stackalloc[T]: Ptr[T] = ???

  /** Stack allocate n values of given type.
   *
   *  Note: unlike alloc, the memory is not zero-initialized.
   */
  def stackalloc[T](n: CSize): Ptr[T] = ???

  def fromCString(cstr: CString): String = new Pointer(cstr.peer).getString(0)
  @inline final def toCString(s: String)(implicit zone: Zone): CString = zone.makeNativeString(s)

  implicit final class CQuote(val ctx: StringContext) extends AnyVal {
    def c(): CString = SWOGHelper.nativeString(ctx.parts.mkString)
  }

  implicit final class UnsafeRichInt(val i: Int) extends AnyVal {
    @inline
    def unary_!(): Int = i
  }
}
