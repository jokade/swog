package test

import scala.scalanative.native._
import scala.scalanative.native.objc._
import scala.scalanative.native.objc.runtime.{ObjCObject, id}

@ObjC
class NSObject extends ObjCObject {
  @returnsThis
  def self(): this.type = extern
  def description(): NSString = extern
}

@ObjCClass
abstract class NSObjectClass extends ObjCObject {
  type InstanceType
  def __cls: id
  def __wrapper: ObjCWrapper[InstanceType]
//  def __wrap(ptr: Ptr[Byte]): InstanceType

  @useWrapper
  @inline def alloc(): InstanceType = extern
}

object NSObject extends NSObjectClass {
  override type InstanceType = NSObject
//  override def __wrap(ptr: Ptr[Byte]): InstanceType = new NSObject(ptr)
}

