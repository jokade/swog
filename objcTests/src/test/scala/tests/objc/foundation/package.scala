package tests.objc

import scalanative._
import unsafe._
import unsigned._
import objc._

package object foundation {

  type NSUInteger = CUnsignedLongLong
  object NSUInteger {
    val MaxValue = ULong.MaxValue
  }

//  implicit final class RichObjCObject(val o: ObjCObject) {
//    def toPtr: id = o.cast[id]
//  }

  type NSStringEncoding = NSUInteger
  object NSStringEncoding {
    val NSUTF8StringEncoding: NSStringEncoding = 4.toULong
  }

}
