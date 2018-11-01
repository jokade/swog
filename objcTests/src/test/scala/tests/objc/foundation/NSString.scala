package tests.objc.foundation

import scalanative.native._
import objc._

@ObjC
class NSString extends NSObject {
  def UTF8String(): Ptr[CSignedChar] = extern
  def length: ULong = extern
  def initWithUTF8String_(nullTerminatedString: CString): NSString = extern
  def stringByPaddingToLength_withString_startingAtIndex_(newLength: Long, padString: NSString, padIndex: ULong): NSString = extern
}

@ObjCClass
abstract class NSStringClass extends NSObjectClass {
  @inline def stringWithCString_encoding_(cString: Ptr[CSignedChar], enc: NSStringEncoding): NSString = extern
}

object NSString extends NSStringClass {
  override type InstanceType = NSString

  def apply(cstring: CString): NSString = stringWithCString_encoding_(cstring,NSStringEncoding.NSUTF8StringEncoding)

  def apply(string: String): NSString = Zone{ implicit z =>
    stringWithCString_encoding_(toCString(string),NSStringEncoding.NSUTF8StringEncoding)
  }

  implicit final class Wrapper(val ns: NSString) extends AnyVal {
    @inline def cstring: CString = ns.UTF8String()

    @inline def string: String = fromCString(ns.UTF8String())
  }
}
