package tests.objc

import de.surfice.smacrotools.debug

import scalanative._
import unsafe._
import objc._
import scala.scalanative.objc.runtime.id

@ObjC
@debug
class Number extends ObjCObject {
  def init(i: Int): Unit = extern
  def get: Int = extern
}

@ObjCClass
@debug
abstract class NumberClass extends ObjCObject {
  type InstanceType
  def __cls: id
  def __ptr: Ptr[Byte] = null
  def alloc(): Number = extern
}

object Number extends NumberClass {
  override type InstanceType = Number
}