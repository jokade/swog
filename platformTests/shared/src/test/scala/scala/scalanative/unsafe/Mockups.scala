package scala.scalanative.unsafe

import de.surfice.smacrotools.debug

@extern
@external
@link("platformtest")
@debug
object Mockups {
  /// returns the passed-in CChar unmodified
  def ptest_return_char(c: CChar): CChar = extern
  /// returns the passed-in CInt unmodified
  def ptest_return_int(i: CInt): CInt = extern
  /// returns the passed-in CLong unmodified
  def ptest_return_long(l: CLong): CInt = extern
  /// returns the passed-in CString unmodified
  def ptest_return_string(s: CString): CString = extern

//  def ptest_get_struct(): Ptr[CStruct1[Int]] = _inst.ptest_struct_get()
}
