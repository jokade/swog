package tests.objc.foundation

import scalanative.native._
import objc._
import scala.scalanative.native.objc.runtime.{ObjCObject, id}

@ObjC
class NSObject extends ObjCObject {
  @inline def init(): this.type = extern
  @inline def retain(): this.type = extern
  @inline def release(): Unit = extern
}

@ObjCClass
abstract class NSObjectClass extends ObjCObject {
  def __cls: id
  type InstanceType
  @inline def alloc(): InstanceType = extern
}

object NSObject extends NSObjectClass {
  type InstanceType = NSObject

}
