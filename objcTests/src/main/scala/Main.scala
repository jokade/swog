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
  id cls = objc_lookUpClass("Bar");
  NSObject* bar = [[cls alloc] init];
//  [bar foo];
  NSLog(@"OK3");
//  NSLog(@"%@",bar);
}
""")
object Main {
  def main(args: Array[String]): Unit = {
    Foo.__cls
    Bar.__cls
//    val foo = Foo.alloc().init()
//    val bar = Bar.alloc()
    
    ext.run()
  }
  
}



