import de.surfice.smacrotools.debug

import scala.scalanative.cobj.{CObjectWrapper, returnsThis}
import scala.scalanative.objc.runtime.id
import scala.scalanative.objc.{ObjC, ObjCClass, ObjCObject}
import scala.scalanative.unsafe._
import scala.scalanative.unsigned.UInt

@extern
object ext {
  def NSLog(s: Ptr[Byte]): Unit = extern
}

@ObjC
class NSObject extends ObjCObject {
  @inline def `class`: id = extern
  @inline def hash: UInt = extern
  @returnsThis
  @inline def init(): this.type = extern
  //  @inline def retain(): this.type = extern
  //  @inline def release(): Unit = extern
}

@ObjCClass
@debug
abstract class NSObjectClass extends ObjCObject {
  type InstanceType
  def __cls: id
  def __ptr = __cls
  //  def __wrapper: CObjectWrapper[InstanceType]
  @inline def alloc()(implicit w: CObjectWrapper[InstanceType]): InstanceType = extern
}

object NSObject extends NSObjectClass {
  type InstanceType = NSObject
}

@ObjC
class NSString extends NSObject {
  type InstanceType = NSString
  def initWithUTF8String_(s: CString): NSString = extern
  def length: CUnsignedLong = extern
}

@ObjCClass
@debug
abstract class NSStringClass extends NSObjectClass {
}

object NSString extends NSStringClass {
  override type InstanceType = NSString
}