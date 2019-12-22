package scala.scalanative.unsafe

import utest._

/**
 * Interop tests for 'extern' objects.
 */
object ExternTest extends TestSuite {
  val tests = Tests {
    'types-{
      'CChar-{
        Mockups.ptest_return_char(Char.MinValue) ==> Char.MinValue
//        Mockups.ptest_return_char(Char.MaxValue) ==> Char.MaxValue
      }
      'CInt-{
        Mockups.ptest_return_int(Int.MinValue) ==> Int.MinValue
        Mockups.ptest_return_int(Int.MaxValue) ==> Int.MaxValue
      }
      'CLong-{
        Mockups.ptest_return_long(Int.MinValue) ==> Int.MinValue
        Mockups.ptest_return_long(Int.MaxValue) ==> Int.MaxValue
//        Mockups.ptest_return_long(Long.MinValue) ==> Long.MinValue
//        Mockups.ptest_return_long(Long.MaxValue) ==> Long.MaxValue
      }
      'CLongLong-{
        
      }
      'CString-{
        val s1 = c"hello world"
        val s2 = Mockups.ptest_return_string(s1)
        fromCString(s2)  ==> "hello world"
      }
    } 
  }
}
