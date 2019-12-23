package scala.scalanative.unsafe

import utest._

/**
 * Interop tests for 'extern' objects.
 */
object ExternTest extends TestSuite {
  val tests = Tests {
    'types-{
      'CChar-{
        Mockups.ptest_return_char(Byte.MinValue) ==> Byte.MinValue
        Mockups.ptest_return_char(Byte.MaxValue) ==> Byte.MaxValue
      }
      'CInt-{
        Mockups.ptest_return_int(Int.MinValue) ==> Int.MinValue
        Mockups.ptest_return_int(Int.MaxValue) ==> Int.MaxValue
      }
      'CLong-{
        val res1: Long = Mockups.ptest_return_long(Long.MinValue)
        res1 ==> Long.MinValue
        val res2: Long = Mockups.ptest_return_long(Long.MaxValue)
        res2 ==> Long.MaxValue
      }
      'CLongLong-{
        Mockups.ptest_return_long_long(Long.MinValue) ==> Long.MinValue
        Mockups.ptest_return_long_long(Long.MaxValue) ==> Long.MaxValue
      }
      'CFloat-{
        Mockups.ptest_return_float(Float.MinPositiveValue) ==> Float.MinPositiveValue
        Mockups.ptest_return_float(Float.MinValue) ==> Float.MinValue
        Mockups.ptest_return_float(Float.MaxValue) ==> Float.MaxValue
      }
      'CDouble-{
        Mockups.ptest_return_double(Double.MinPositiveValue) ==> Double.MinPositiveValue
        Mockups.ptest_return_double(Double.MinValue) ==> Double.MinValue
        Mockups.ptest_return_double(Double.MaxValue) ==> Double.MaxValue
      }
      'CString-{
        val s1 = c"hello world"
        val s2 = Mockups.ptest_return_string(s1)
        fromCString(s2)  ==> "hello world"
      }
    } 
  }
}
