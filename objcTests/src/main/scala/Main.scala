

import de.surfice.smacrotools.debug
import test._

import scalanative.native._
import objc._
import scala.scalanative.native.objc.runtime.{ObjCObject, id}

@ObjC
class NSDate extends NSObject {
}

@ObjCClass
abstract class NSDateClass extends NSObjectClass {
  def date(): NSDate = extern
}

object NSDate extends NSDateClass {
  override type InstanceType = NSDate
}


@ObjC
@debug
class NSMutableArray[T<:NSObject] extends NSObject {
  def addObject_(obj: T): Unit = extern
  def insertObject_atIndex_(anObject: T, index: CUnsignedLong): Unit = extern
  def objectAtIndex_(index: CUnsignedLong): NSObject = extern
}


@ObjCClass
abstract class NSMutableArrayClass extends NSObjectClass {
  def arrayWithCapacity_[T<:NSObject](numItems: CUnsignedLong): NSMutableArray[T] = extern
}

object NSMutableArray extends NSMutableArrayClass {
  override type InstanceType = NSMutableArray[_]
}


object Main {
  def main(args: Array[String]): Unit = Zone { implicit z =>
    val date = NSDate.date().retain()
    val str = NSString(c"Hello world!")
    val array = NSMutableArray.arrayWithCapacity_[NSObject](1.toUInt).retain()
    array.addObject_(date)
    array.insertObject_atIndex_(str,0.toUInt)
    NSLog(array.objectAtIndex_(0.toUInt))
  }

  def NSLog(str: NSString): Unit = Foundation.NSLog(str.__ptr)
  def NSLog(obj: NSObject) = Foundation.NSLog(NSString(c"%@").__ptr,obj.__ptr)
}


@extern
object Foundation {
  def NSLog(fmt: Ptr[Byte]): Unit = extern
  def NSLog(fmt: Ptr[Byte], obj: Ptr[Byte]): Unit = extern
}

/*
@ScalaObjC
@debug
class MyClass(self: id) extends NSObject {
  def foo(): Unit = {
    val s = $super[NSObject,NSString](self)(_.description())
    Main.NSLog(NSString(c"%@"),s)
    println("FOO")
  }

  def bar(): MyClass = this

}

object MyClass extends NSObjectClass {
  override type InstanceType = MyClass
}

@ObjC
trait Foo extends ObjCObject {
  def bar(): Unit = extern

}
*/