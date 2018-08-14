package tests.objc

import scala.scalanative.native._
import scala.scalanative.native.objc.runtime.{ObjCObject, id}

package object foundation {

  type NSUInteger = CUnsignedLongLong

  implicit final class RichObjCObject(val o: ObjCObject) {
    def toPtr: id = o.cast[id]
  }

  type NSStringEncoding = NSUInteger
  object NSStringEncoding {
    val NSUTF8StringEncoding: NSStringEncoding = 4.toULong
  }

}
