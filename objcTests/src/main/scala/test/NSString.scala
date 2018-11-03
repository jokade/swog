package test

import de.surfice.smacrotools.debug

import scala.scalanative.native._
import scala.scalanative.native.objc._

@ObjC
class NSString extends NSObject {

}


@ObjCClass
abstract class NSStringClass extends NSObjectClass {
  @inline def stringWithCString_encoding_(str: CString, enc: ULong): NSString = extern
}

object NSString extends NSStringClass {
  override type InstanceType = NSString

  def apply(s: CString): NSString = stringWithCString_encoding_(s,5.toUInt)
//  implicit override def __wrap(ptr: Ptr[Byte]): NSString = new NSString(ptr)

//  implicit object __wrapper extends ObjCWrapper[NSString] {
//    override def __wrap(ptr: Ptr[Byte]): NSString = new NSString(ptr)
//  }
}
