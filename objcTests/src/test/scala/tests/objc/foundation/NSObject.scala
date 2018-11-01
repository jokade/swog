package tests.objc.foundation

import de.surfice.smacrotools.debug

import scalanative.native._
import objc._
import scala.scalanative.native.objc.runtime.{ObjCObject, id}

@ObjC
@debug
class NSObject extends ObjCObject {

  @inline def `class`: id = extern
  @inline def hash: UInt = extern
  @inline def init(): this.type = extern
  @inline def retain(): this.type = extern
  @inline def release(): Unit = extern
}

@ObjCClass
@debug
abstract class NSObjectClass extends ObjCObject {
  def __cls: id
  type InstanceType
  @inline def alloc(): InstanceType = extern
}

object NSObject extends NSObjectClass {
  type InstanceType = NSObject
}
