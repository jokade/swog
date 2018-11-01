package tests.objc

import de.surfice.smacrotools.debug
import scalanative.native._
import objc._
import runtime._
import utest._
import foundation._

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

}


