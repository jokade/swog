import de.surfice.smacrotools.debug

import scalanative._
import unsafe._
import objc._
import scala.scalanative
import scala.scalanative.annotation.InlineSource
import scala.scalanative.cobj.CObjectWrapper

@InlineSource("ObjC",
"""
#include <objc/runtime.h>
#import <Foundation/Foundation.h>

void run() {
  NSLog(@"called");
  id cls = objc_getClass("Foo");
  NSObject* foo = [cls alloc];
  NSLog(@"%@",foo);
}
""")
object Main {
  def main(args: Array[String]): Unit = {
    Foo.__cls
    ext.run()
  }
}


@ScalaObjC
@debug
class Foo extends NSObject {
}

@ObjCClass
abstract class FooClass extends NSObjectClass

object Foo extends FooClass {
  override type InstanceType = Foo
  def myAlloc()(implicit w: CObjectWrapper[InstanceType]): InstanceType = {
    println("mark4")
    alloc()
  }
}