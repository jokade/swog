package tests.objc

import de.surfice.smacrotools.debug
import scalanative.native._
import objc._
import runtime._
import utest._

object ObjCTest extends TestSuite {

  val tests = Tests{
    'NSObject-{
      val obj = NSObject.alloc().init()
      fromCString( class_getName( obj.`class` ) ) ==> "NSObject"
    }
    'NSString-{
      'callSuperclassMethod-{
        val str = NSString.alloc().init()
        str.length ==> 0.toULong
      }
      'selectorWithArg-{
        val str = NSString.alloc().initWithUTF8String_(c"foo")
        str.length ==> 3.toULong
      }
      'selectorWithMultipleSegments-{
        val str = NSString.alloc().initWithUTF8String_(c"foo")
        val pad = NSString.alloc().initWithUTF8String_(c"bar")
        val str2 = str.stringByPaddingToLength_withString_startingAtIndex_(6,pad,0.toULong)
        str2.length ==> 6.toULong
      }
    }
  }

  @ObjC
  @debug
  class NSObject {
    @inline def `class`: id = extern
    @inline def hash: UInt = extern
    @inline def init(): this.type = extern
    @inline def retain(): this.type = extern
  }

  @ObjCClass
  abstract class NSObjectClass {
    type InstanceType
    def __cls: id
    def alloc(): InstanceType = extern
  }

  object NSObject extends NSObjectClass {
    override type InstanceType = NSObject
  }

  @ObjC
  class NSString extends NSObject {
    def length: ULong = extern
    def initWithUTF8String_(nullTerminatedString: CString): NSString = extern
    def stringByPaddingToLength_withString_startingAtIndex_(newLength: Long, padString: NSString, padIndex: ULong): NSString = extern
  }

  abstract class NSStringClass extends NSObjectClass {
    override type InstanceType = NSString
    def __cls: id
  }

  object NSString extends NSStringClass
}
