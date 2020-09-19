package tests.objc.foundation

import de.surfice.smacrotools.debug
import tests.objc.foundation

import scalanative._
import unsafe._
import unsigned._
import objc._
import scala.scalanative.cobj.CObj.CObjWrapper
import scala.scalanative.cobj.CObjectWrapper
import scala.scalanative.objc.runtime.id

@ObjC
@debug
class NSObject extends ObjCObject {
  @inline def `class`: id = extern
  @inline def hash: UInt = extern
//  @inline def init()(implicit w: CObjectWrapper[InstanceType]): InstanceType = extern
//  @inline def retain(): this.type = extern
//  @inline def release(): Unit = extern
}

@ObjCClass
@debug
abstract class NSObjectClass {
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
}