package scala.scalanative

import com.sun.jna.{Callback, NativeLong, Pointer, SWOGHelper}

import scala.reflect.runtime.universe._
import scala.scalanative.unsigned.{UByte, UInt, ULong, UShort}
import scala.language.experimental.macros

package object unsafe {
  type Word              = Long
  type UWord             = ULong

  // TODO: check type mappings and correct
  type CBool             = Boolean
  type CChar             = Byte
  type CSignedChar       = Char
  type CUnsignedChar     = UByte
  type CShort            = Short
  type CUnsignedShort    = UShort
  type CInt              = Int
  type CLongInt          = Int
  type CUnsignedInt      = UInt
  type CUnsignedLongInt  = UInt
  type CLong             = NativeLong
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

  def extern: Nothing = throw new UnsupportedOperationException("A call to 'extern' was not properly transformed.")

  /** Stack allocate a value of given type.
   *
   *  Note: unlike alloc, the memory is not zero-initialized.
   */
  def stackalloc[T](implicit tag: WeakTypeTag[T]): Ptr[T] = macro MacroImpl.stackalloc[T]

  /** Stack allocate n values of given type.
   *
   *  Note: unlike alloc, the memory is not zero-initialized.
   */
  def stackalloc[T](n: CSize)(implicit tag: WeakTypeTag[T]): Ptr[T] = macro MacroImpl.stackallocN[T]

  /** Heap allocate and zero-initialize a value.
   */
  def alloc[T]: Ptr[T] = macro MacroImpl.alloc[T]

  @inline final def fromCString(cstr: CString): String = cstr.getString(0)

  @inline final def toCString(s: String)(implicit zone: Zone): CString = zone.makeNativeString(s)

  implicit def longToCLong(l: Long): CLong = new NativeLong(l)
  implicit def intToCLong(i: Int): CLong = new NativeLong(i)
  implicit def clongToLong(l: CLong): Long = l.longValue()
  implicit def clongToInt(l: CLong): Int = l.intValue()
  
  implicit final class CQuote(val ctx: StringContext) extends AnyVal {
    def c(): CString = SWOGHelper.nativeString(ctx.parts.mkString)
  }


}
