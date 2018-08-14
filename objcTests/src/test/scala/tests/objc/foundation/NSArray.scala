package tests.objc.foundation

import scalanative.native._
import objc._
import scala.scalanative.native.objc.runtime.{ObjCObject, id}

@ObjC
class NSArray[T<:ObjCObject] extends NSObject {
  @inline def objectAtIndex_(index: NSUInteger): T = extern
  @inline def sortedArrayUsingComparator_(comparator: Block2[NSString,NSString,Int]): NSArray[T] = extern
}

@ObjCClass
abstract class NSArrayClass extends NSObjectClass {
  @inline def arrayWithObjects_count_[T<:ObjCObject](objects: Ptr[id], cnt: NSUInteger): NSArray[T] = extern
}

object NSArray extends NSArrayClass {
  override type InstanceType = NSArray[_]

  def apply[T<:NSObject](objects: T*): NSArray[T] = arrayWithObjects(objects)

  def arrayWithObjects[T<:ObjCObject](objects: Seq[T]): NSArray[T] = Zone { implicit z =>
    val count = objects.size
    val array = stackalloc[id]( sizeof[id] * count)
    for(i<-0 until count)
      !(array + i) = objects(i).toPtr
    arrayWithObjects_count_(array,count.toULong)
    //objc_msgSend(__cls,__sel_arrayWithObjects_cnt_,array,count).cast[NSArray[T]]
  }

  implicit final class RichNSArray[T <: NSObject](val ns: NSArray[T]) extends AnyVal {
    def apply(idx: Int): T = ns.objectAtIndex_(idx.toUInt)
  }
}


