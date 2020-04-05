package tests.objc

import de.surfice.smacrotools.debug
import tests.objc.foundation.NSUInteger
import utest._

import scalanative._
import objc._
import unsafe._
import unsigned._
import scala.scalanative.annotation.InlineSource
import scala.scalanative.unsafe.Tag.UByte

object BasicTests extends TestSuite {

  val EUT = BasicTestsEUT

  val tests = Tests{
    'returnTypes - {
      'Unit - {
        val unit = ()
        EUT.noop() ==>  unit
      }
      'Byte - {
        EUT.getByteMin() ==> Byte.MinValue
        EUT.getByteMax() ==> Byte.MaxValue
      }
      'UByte - {
        // TODO: the '==>' syntax doesn't work for unsigned => why?
        assert(EUT.getUByteMax() == 255.toUByte)
      }
      'Short - {
        EUT.getShortMin() ==> Short.MinValue
        EUT.getShortMax() ==> Short.MaxValue
      }
      'UShort - {
        assert(EUT.getUShortMax() == UShort.MaxValue)
      }
      'Int - {
        EUT.getIntMin() ==> Int.MinValue
        EUT.getIntMax() ==> Int.MaxValue
      }
      'UInt - {
        assert(EUT.getUIntMax() == UInt.MaxValue)
      }
      'Long - {
        EUT.getLongMin() ==> Long.MinValue
        EUT.getLongMax() ==> Long.MaxValue
      }
      'ULong - {
        assert(EUT.getULongMax() == ULong.MaxValue)
      }
      'BOOL - {
        EUT.getBoolFalse() ==> false
        EUT.getBoolTrue() ==> true
      }
      'Char - {
        EUT.getChar() ==> 'a'
      }
      'CString - {
        fromCString(EUT.getCString()) ==> "Hello, world!"
      }
      'Double - {
        // TODO: check reason for deviation between Scala<->C w.r.t. min pos value
        //        EUT.getDoubleMinPositiveValue() ==> Double.MinPositiveValue
        println(EUT.getDoubleMax())
        println(Double.MaxValue)
        println(Double.MinPositiveValue)
        println(Double.MinValue)
        EUT.getDoubleMax() ==> Double.MaxValue
      }
      'Float - {
        EUT.getFloatMax() ==> Float.MaxValue
      }
      'NSUInteger - {
        assert( EUT.getNSUIntegerMax() == NSUInteger.MaxValue )
      }
    }
    'argTypes - {
      'Byte - {
        EUT.incByte_(Byte.MinValue) ==> Byte.MinValue + 1
        EUT.incByte_((Byte.MaxValue - 1).toByte) ==> Byte.MaxValue
      }
      'Ubyte - {
        assert( EUT.incUByte_(0.toUByte) == 1.toUByte )
        assert( EUT.incUByte_(254.toUByte) == 255.toUByte )
      }
      'Short - {
        EUT.incShort_(Short.MinValue) ==> Short.MinValue + 1
        EUT.incShort_((Short.MaxValue - 1).toShort)   ==> Short.MaxValue
      }
      'UShort - {
        assert( EUT.incUShort_(0.toUShort) == 1.toUShort )
        assert( EUT.incUShort_(65534.toUShort) == 65535.toUShort )
      }
      'Int - {
        EUT.incInt_(Int.MinValue) ==> Int.MinValue + 1
        EUT.incInt_(Int.MaxValue - 1) ==> Int.MaxValue
      }
      'UInt - {
        assert( EUT.incUInt_(0.toUInt) == 1.toUInt )
        assert( EUT.incUInt_(UInt.MaxValue - 1.toUInt) == UInt.MaxValue )
      }
      'Long - {
        EUT.incLong_(Long.MinValue) ==> Long.MinValue + 1
        EUT.incLong_(Long.MaxValue - 1L) ==> Long.MaxValue
      }
      'ULong - {
        assert( EUT.incULong_(0L.toULong) == 1L.toULong )
        assert( EUT.incULong_(ULong.MaxValue - 1L.toULong) == ULong.MaxValue )
      }
      'Boolean - {
        EUT.negateBool_(false) ==> true
        EUT.negateBool_(true) ==> false
      }
      'String - {
        Zone{ implicit z =>
          // TODO: passing null fails
//          fromCString( EUT.passString_(null) ) ==> null
          fromCString( EUT.passString_(toCString("")) ) ==> ""
          fromCString( EUT.passString_(toCString("Hello, world!")) ) ==> "Hello, world!"
        }
        'Object - {
          val eut = EUT.alloc()
          fromCString( EUT.getName_(eut) ) ==> "EUT"
        }
      }
    }

    'instanceMethods-{
      val eut = EUT.alloc()
      'noArg-{
        'int-{
          eut.maxInt ==> Int.MaxValue
        }
        'string-{
          fromCString( eut.name ) ==> "EUT"
        }
        'unit-{

        }
      }
    }
  }

}

@ObjC
class BasicTestsEUT extends ObjCObject {
  def name: CString = extern
  def maxInt: Int = extern
}

@ObjCClass
@debug
abstract class BasicTestsEUTClass extends ObjCClassObject {
  def noop(): Unit = extern
  def getByteMin(): Byte = extern
  def getByteMax(): Byte = extern
  def getUByteMax(): UByte = extern
  def getShortMin(): Short = extern
  def getShortMax(): Short = extern
  def getUShortMax(): UShort = extern
  def getIntMin(): Int = extern
  def getIntMax(): Int = extern
  def getUIntMax(): UInt = extern
  def getLongMin(): Long = extern
  def getLongMax(): Long = extern
  def getULongMax(): ULong = extern
  def getBoolTrue(): Boolean = extern
  def getBoolFalse(): Boolean = extern
  def getChar(): Char = extern
  def getCString(): CString = extern
  def getDoubleMinPositiveValue(): Double = extern
  def getDoubleMax(): Double = extern
  def getFloatMax(): Float = extern
  def getNSUIntegerMax(): NSUInteger = extern
  def incByte_(b: Byte): Byte = extern
  def incUByte_(b: UByte): UByte = extern
  def incShort_(s: Short): Short = extern
  def incUShort_(s: UShort): UShort = extern
  def incInt_(i: Int): Int = extern
  def incUInt_(i: UInt): UInt = extern
  def incLong_(l: Long): Long = extern
  def incULong_(l: ULong): ULong = extern
  def negateBool_(f: Boolean): Boolean = extern
  def passString_(s: CString): CString = extern
  def alloc(): BasicTestsEUT = extern
  def getName_(eut: BasicTestsEUT): CString = extern
}

@InlineSource("ObjC",
"""
#include <Foundation/Foundation.h>
#include <limits.h>
#include <float.h>

@interface BasicTestsEUT : NSObject
  + (void)noop;
  + (char)getByteMin;
  + (char)getByteMax;
  + (unsigned char)getUByteMax;
  + (short)getShortMin;
  + (short)getShortMax;
  + (unsigned short)getUShortMax;
  + (int)getIntMin;
  + (int)getIntMax;
  + (unsigned int)getUIntMax;
  + (long)getLongMax;
  + (long)getLongMin;
  + (unsigned long)getULongMax;
  + (BOOL)getBoolTrue;
  + (BOOL)getBoolFalse;
  + (char)getChar;
  + (char*)getCString;
  + (double)getDoubleMin;
  + (double)getDoubleMax;
  + (float)getFloatMax;
  + (NSUInteger)getNSUIntegerMax;
  + (char)incByte:(char)i;
  + (unsigned char)incUByte:(unsigned char)i;
  + (short)incShort:(short)i;
  + (unsigned short)incUShort:(unsigned short)i;
  + (int)incInt:(int)i;
  + (unsigned int)incUInt:(unsigned int)i;
  + (long)incLong:(long)l;
  + (unsigned long)incULong:(unsigned long)l;
  + (BOOL)negateBool:(BOOL)f;
  + (char *)passString:(char *)s;
  + (char *)getName:(BasicTestsEUT *)eut;
  - (char*)name;
  - (int)maxInt;
@end

@implementation BasicTestsEUT
  + (void)noop {}
  + (char)getByteMin { return CHAR_MIN; }
  + (char)getByteMax { return CHAR_MAX; }
  + (unsigned char)getUByteMax { return UCHAR_MAX; }
  + (short)getShortMin { return SHRT_MIN; }
  + (short)getShortMax { return SHRT_MAX; }
  + (unsigned short)getUShortMax { return USHRT_MAX; }
  + (int)getIntMin { return INT_MIN; }
  + (int)getIntMax { return INT_MAX; }
  + (unsigned int)getUIntMax { return UINT_MAX; }
  + (long)getLongMin { return LONG_MIN; }
  + (long)getLongMax { return LONG_MAX; }
  + (unsigned long)getULongMax { return ULONG_MAX; }
  + (BOOL)getBoolTrue { return YES; }
  + (BOOL)getBoolFalse { return NO; }
  + (char)getChar { return 'a'; }
  + (char*)getCString { return "Hello, world!"; }
  + (double)getDoubleMin { return DBL_MIN; }
  + (double)getDoubleMax { return DBL_MAX; }
  + (float)getFloatMax { return FLT_MAX; }
  + (NSUInteger)getNSUIntegerMax { return NSUIntegerMax; }
  + (char)incByte:(char)i { return i+1; }
  + (unsigned char)incUByte:(unsigned char)i { return i+1; }
  + (short)incShort:(short)i { return i+1; }
  + (unsigned short)incUShort:(unsigned short)i { return i+1; }
  + (int)incInt:(int)i { return i+1; }
  + (unsigned int)incUInt:(unsigned int)i { return i+1; }
  + (long)incLong:(long)l { return l+1; }
  + (unsigned long)incULong:(unsigned long)l { return l+1; }
  + (BOOL)negateBool:(BOOL)f { return !f; }
  + (char *)passString:(char *)s { return s; }
  + (char *)getName:(BasicTestsEUT *)eut { return [eut name]; }
  - (char*)name { return "EUT"; }
  - (int)maxInt { return INT_MAX; }
@end
""")
object BasicTestsEUT extends BasicTestsEUTClass {
}

