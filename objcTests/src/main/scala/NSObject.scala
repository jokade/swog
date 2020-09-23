import de.surfice.smacrotools.debug

import scala.scalanative.cobj.{CObjectWrapper, returnsThis}
import scala.scalanative.objc.runtime.id
import scala.scalanative.objc.{ObjC, ObjCClass, ObjCObject, ScalaObjC}
import scala.scalanative.unsafe._
import scala.scalanative.unsigned.UInt

@extern
object ext {
  def NSLog(s: Ptr[Byte]): Unit = extern
  def run(): Unit = extern
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
abstract class NSObjectClass {
  type InstanceType
  def __cls: id
//  def __ptr = __cls
  //  def __wrapper: CObjectWrapper[InstanceType]
  @inline def alloc()(implicit w: CObjectWrapper[InstanceType]): InstanceType = extern
}

object NSObject extends NSObjectClass {
  type InstanceType = NSObject
}

