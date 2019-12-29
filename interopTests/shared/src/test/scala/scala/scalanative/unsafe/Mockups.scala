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

  def ptest_struct1_new(): Ptr[CStruct1[CInt]] = extern
  
  def ptest_struct2_new(): Ptr[CStruct2[CChar,CLong]] = extern
  
  def ptest_struct3_new(): Ptr[CStruct3[CShort,CString,CInt]] = extern
  
  def ptest_struct4_new(): Ptr[CStruct4[CChar,CShort,CStruct1[CChar],CLongLong]] = extern
  
  def ptest_incr_int_ptr(i: Ptr[CInt]): Unit = extern

  type PTestNumStruct = CStruct2[CLongLong,CInt]

  def ptest_incr_num_struct(p: Ptr[PTestNumStruct]): Unit = extern
//  def ptest_get_struct(): Ptr[CStruct1[Int]] = _inst.ptest_struct_get()
}
