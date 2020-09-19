import de.surfice.smacrotools.debug

import scala.scalanative.cobj.returnsThis
import scala.scalanative.objc.{ObjC, ObjCClass}
import scala.scalanative.unsafe.{CString, CUnsignedLong, extern}

@ObjC
class NSString extends NSObject {
  type InstanceType = NSString
  @returnsThis
  def initWithUTF8String_(s: CString): NSString = extern
  def length: CUnsignedLong = extern
}

@ObjCClass
abstract class NSStringClass extends NSObjectClass {
}

object NSString extends NSStringClass {
  override type InstanceType = NSString
}

