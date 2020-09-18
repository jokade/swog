package test.interop

import test.interop.Mockups.PTestNumStruct
import utest._

import scala.scalanative._
import scala.scalanative.unsafe._
import scala.scalanative.unsigned._
import interop._

/**
 * Interop tests for 'extern' objects.
 */
object ExternTest extends TestSuite {
  val tests = Tests {
    'types-{
      'CBool-{
        Mockups.ptest_return_bool(false) ==> false
        Mockups.ptest_return_bool(true) ==> true
      }
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
//          s._3._1 ==> 'x'
        }
      }
    } 

    'global-{
      'int-{
        Mockups.ptest_global_int ==> 12345678
      }
    }

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
      'zone-{
        'CInt-{
          Zone{ implicit z =>
            val p = alloc[CInt]

            p := -1
            !p ==> -1
            Mockups.ptest_incr_int_ptr(p)
          }
        }
      }

      'Ptr-{
        'null-{
          Mockups.ptest_return_ptr(null) ==> null
        }
        'CBool-{
          val p = stackalloc[CBool]
          p := false
          !p ==> false
          p := true
          !p ==> true
        }
        'CUnsignedChar-{
          val p = stackalloc[CUnsignedChar]
          p := 0xFF.toUByte
          assert( !p == 0xFF.toUByte )
        }
        'CUnsignedShort-{
          val p = stackalloc[CUnsignedShort]
          p := 0xFFFF.toUShort
          assert( !p == 0xFFFF.toUShort )
        }
        'CUnsignedInt-{
          val p = stackalloc[CUnsignedInt]
          p := 4321.toUInt
          assert( !p == 4321.toUInt )
        }
        'CUnsignedLong-{
          val p = stackalloc[CUnsignedLong]
          p := 0xFFFFFFFF.toULong
          assert( !p == 0xFFFFFFFF.toULong )
        }
        'CFloat-{
          val p = stackalloc[CFloat]
          p := 1.2345F
          !p ==> 1.2345F
        }
        'CDouble-{
          val p = stackalloc[CDouble]
          p := 1.23456789
          !p ==> 1.23456789
        }
        'CString-{
          val p = stackalloc[CString]
          p := c"hello world"
          fromCString(!p) ==> "hello world"
        }
        'index-{
          val p = stackalloc[CInt](4)
          p(0) = 0
          p(1) = 123456789
          p(2) = Int.MinValue
          p(3) = Int.MaxValue

          p(0) ==> 0
          p(1) ==> 123456789
          p(2) ==> Int.MinValue
          p(3) ==> Int.MaxValue
        }
      }

      'CFunc- {
        'CFunc0 - {
          //val f = new CFuncPtr0[CInt] { def apply(): Int = 42 }
          val f = new CB0 { def apply(): Int = 42 }
          Mockups.ptest_call_func0(f) ==> 42
        }
      }
    }
  }
}

