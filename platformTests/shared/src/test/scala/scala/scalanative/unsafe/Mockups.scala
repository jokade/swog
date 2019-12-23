package scala.scalanative.unsafe

import de.surfice.smacrotools.debug

@extern
@external("platformtest")
@debug
object Mockups {
  
  /// returns the passed-in CChar unmodified
  def ptest_return_char(c: CChar): CChar = extern
  /// returns the passed-in CInt unmodified
  def ptest_return_int(i: CInt): CInt = extern
  /// returns the passed-in CLong unmodified
  def ptest_return_long(l: CLong): CLong = extern
  /// returns the passed-in CLongLong unmodified
  def ptest_return_long_long(l: CLongLong): CLongLong = extern
  /// returns the passed-in CFloat unmodified
  def ptest_return_float(f: CFloat): CFloat = extern
  /// returns the passed-in CDouble unmodified
  def ptest_return_double(f: CDouble): CDouble = extern
  /// returns the passed-in CString unmodified
  def ptest_return_string(s: CString): CString = extern

//  def ptest_get_struct(): Ptr[CStruct1[Int]] = _inst.ptest_struct_get()
}
