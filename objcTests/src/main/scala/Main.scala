

import de.surfice.smacrotools.debug
import test._

import scalanative.native._
import objc._
import scala.scalanative.native.objc.runtime.id

object Main {
//  implicit object NSStringWrapper extends Wrapper[NSString] {
//    override def wrap(ptr: Ptr[Byte]): NSString = new NSString(ptr)
//  }

  def main(args: Array[String]): Unit = Zone { implicit z =>
//    val array = NSMutableArray.array[NSString]()
//    array.addObject_(NSString(c"Hello"))
//    array.addObject_(NSString(c"World"))
//    array(1) = NSString(c"FOO")
//    array.addObject_(str)
//    stdio.printf(c"%x\n",foo.__ptr)
//    ext.NSLog(str.__ptr,array.objectAtIndex_(0.toUInt).__ptr)
//    NSLog(NSString(c"%@"),array)
//    val d = NSString(c"42.0")
//    println( d.doubleValue() )
//    println( d.floatValue() )
    val my = MyClass.alloc()
    my.foo()
    NSLog(NSString(c"%@"),my)
    println("DONE")
  }

  def NSLog(format: NSString, arg1: NSObject): Unit = ext.NSLog(format.__ptr,arg1.__ptr)
}

@extern
object ext {
  def NSLog(format: Ptr[Byte], args: CVararg*): Unit = extern
}

@ScalaObjC
@debug
class MyClass(self: id) extends NSObject {
  def foo(): Unit = {
    val s = $super[NSObject,NSString](self)(_.description())
    Main.NSLog(NSString(c"%@"),s)
    println("FOO")
  }
}

object MyClass extends NSObjectClass {
  override type InstanceType = MyClass
}
