package test

import de.surfice.smacrotools.debug

import scalanative.native._
import objc._

@ObjC
@debug
class NSMutableArray[T<:NSObject] extends NSObject {
  @inline def addObject_(anObject: NSObject): Unit = extern
  @inline def objectAtIndex_(index: CUnsignedLong): Ptr[Byte] = extern
  @inline def replaceObjectAtIndex_withObject_(index: CUnsignedLong, anObject: Ptr[Byte]): Unit = extern
  def apply(idx: Int)(implicit wrapper: ObjCWrapper[T]): T =
    wrapper.__wrap(objectAtIndex_(idx.toULong))
  def update(idx: Int, obj: T): Unit = replaceObjectAtIndex_withObject_(idx.toULong,obj.__ptr)
}

@ObjCClass
abstract class NSMutableArrayClass extends NSObjectClass {
  @inline def array[T<:NSObject](): NSMutableArray[T] = extern
}


object NSMutableArray extends NSMutableArrayClass {
  override type InstanceType = NSMutableArray[_]
//  override def __wrap(ptr: Ptr[Byte]): NSMutableArray[_] = null
}

