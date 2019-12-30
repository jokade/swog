package test.interop

import utest._

import scala.scalanative._
import interop._
import unsafe._
import unsigned._

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
      'CUnsignedChar-{
        assert( Mockups.ptest_return_uchar(UByte.MinValue) == UByte.MinValue )
        assert( Mockups.ptest_return_uchar(UByte.MaxValue) == UByte.MaxValue )
        assert( Mockups.ptest_return_uchar(34.toUByte) == 34.toUByte )
      }
      'CUnsignedShort-{
        assert( Mockups.ptest_return_ushort(UShort.MinValue) == UShort.MinValue )
        assert( Mockups.ptest_return_ushort(UShort.MaxValue) == UShort.MaxValue )
        assert( Mockups.ptest_return_ushort(1234.toUShort) == 1234.toUShort )
      }
      'CUnsigedInt-{
        assert( Mockups.ptest_return_uint(UInt.MinValue) == UInt.MinValue )
        assert( Mockups.ptest_return_uint(UInt.MaxValue) == UInt.MaxValue )
        assert( Mockups.ptest_return_uint(12345678.toUInt) == 12345678.toUInt )
      }
      'CUnsignedLong-{
        assert( Mockups.ptest_return_ulong(ULong.MinValue) == ULong.MinValue )
        assert( Mockups.ptest_return_ulong(ULong.MaxValue) == ULong.MaxValue )
        assert( Mockups.ptest_return_ulong(12345678.toULong) == 12345678.toULong )
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

      'CStruct-{
        'CInt-{
          val s = Mockups.ptest_struct1_new()
          s._1 ==> 42
          s._1 = Int.MaxValue
          s._1 ==> Int.MaxValue
        }
        'CChar_CLong-{
          val s = Mockups.ptest_struct2_new() 
          s._1 ==> 'a'
          s._1 = 'X'.toByte
          s._1 ==> 'X'
          
          val l: Long = s._2
          l ==> 123456789
          s._2 = Long.MinValue
          val l2: Long = s._2
          l2 ==> Long.MinValue
        }
        'CShort_CString_CInt-{
          val s = Mockups.ptest_struct3_new()
          
          s._1 ==> -4321
          s._1 = Short.MaxValue
          s._1 ==> Short.MaxValue

          fromCString(s._2) ==> "Hello, world!"
          s._2 = c"another string"
          fromCString(s._2) ==> "another string"
          
          s._3 ==> -1234567
          s._3 = Int.MinValue
          s._3 ==> Int.MinValue
        }
        'CChar_CShort_CStruct1_CLongLong-{
          val s = Mockups.ptest_struct4_new()
          
          s._1 ==> 'c'
          s._2 ==> 99
          s._3._1 ==> 'x'
        }
      }
    } 

    'global-{
      'int-{
        Mockups.ptest_global_int ==> 12345678
      }
    }
/*
    'alloc-{
      'stackalloc-{
        'CInt-{
          val p = stackalloc[CInt]

          p := 42
          Mockups.ptest_incr_int_ptr(p)
          !p ==> 43
        }
        'CStruct-{
          val p = stackalloc[PTestNumStruct]

          p._1 = -1
          p._2 = 42

          p._1 ==> -1
          p._2 ==> 42

          Mockups.ptest_incr_num_struct(p)

          p._1 ==> 0
          p._2 ==> 43
        }
      }
    }
    */
  }
}

