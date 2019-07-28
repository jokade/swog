package tests.objc

import de.surfice.smacrotools.debug
import utest._

import scalanative._
import objc._
import unsafe._
import scala.scalanative.annotation.InlineSource

object ObjCTests extends TestSuite {

  val tests = Tests{
    'returnTypes-{
      'Int-{
        EUT.getIntMin() ==> Int.MinValue
        EUT.getIntMax() ==> Int.MaxValue
      }
      'Long-{
        EUT.getLongMin() ==> Long.MinValue
        EUT.getLongMax() ==> Long.MaxValue
      }
      'BOOL-{
        EUT.getBoolFalse() ==> false
        EUT.getBoolTrue() ==> true
      }
      'Char-{
        EUT.getChar() ==> 'a'
      }
      'CString-{
        fromCString(EUT.getCString()) ==> "Hello"
      }
      'Double-{
        EUT.getDoubleMax() ==> Double.MaxValue
      }
      'Float-{
        EUT.getFloatMax() ==> Float.MaxValue
      }
      'Object-{
        println( EUT.alloc() )
      }
    }
    'args-{
      'Int-{
        EUT.incInt_(42) ==> 43
      }
      'Long-{
        EUT.incLong_(123456789123456789L) ==> 123456789123456790L
      }
    }
  }

}

@ObjC
class EUT extends ObjCObject {
  def name: CString = extern
}

@ObjCClass
@debug
abstract class ClassTests extends ObjCClassObject {
  def getIntMin(): Int = extern
  def getIntMax(): Int = extern
  def getLongMin(): Long = extern
  def getLongMax(): Long = extern
  def getBoolTrue(): Boolean = extern
  def getBoolFalse(): Boolean = extern
  def getChar(): Char = extern
  def getCString(): CString = extern
  def getDoubleMax(): Double = extern
  def getFloatMax(): Float = extern
  def incInt_(i: Int): Int = extern
  def incLong_(l: Long): Long = extern
  def alloc(): EUT = extern
}

@InlineSource("ObjC",
"""
#include <Foundation/Foundation.h>
#include <limits.h>
#include <float.h>

@interface EUT : NSObject
  + (int)getIntMin;
  + (int)getIntMax;
  + (long)getLongMax;
  + (long)getLongMin;
  + (BOOL)getBoolTrue;
  + (BOOL)getBoolFalse;
  + (char)getChar;
  + (char*)getCString;
  + (double)getDoubleMax;
  + (float)getFloatMax;
  + (int)incInt:(int)i;
  + (long)incLong:(long)l;
  - (char*)name;
@end

@implementation EUT
  + (int)getIntMin { return INT_MIN; }
  + (int)getIntMax { return INT_MAX; }
  + (long)getLongMin { return LONG_MIN; }
  + (long)getLongMax { return LONG_MAX; }
  + (BOOL)getBoolTrue { return YES; }
  + (BOOL)getBoolFalse { return NO; }
  + (char)getChar { return 'a'; }
  + (char*)getCString { return "Hello"; }
  + (double)getDoubleMax { return DBL_MAX; }
  + (float)getFloatMax { return FLT_MAX; }
  + (int)incInt:(int)i { return i+1; }
  + (long)incLong:(long)l { return l+1; }
  - (char*)name { return "EUT"; }
@end
""")
object EUT extends ClassTests {
}
