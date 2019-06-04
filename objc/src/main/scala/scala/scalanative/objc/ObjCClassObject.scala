package scala.scalanative.objc

import scala.scalanative.objc.runtime.id
import scala.scalanative.unsafe.Ptr

abstract class ObjCClassObject extends ObjCObject {
  type InstanceType
  def __cls: id
  def __ptr: Ptr[Byte] = null
}
