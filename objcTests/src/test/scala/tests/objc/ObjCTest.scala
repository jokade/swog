package tests.objc

import de.surfice.smacrotools.debug
import scalanative.native._
import objc._
import runtime._
import utest._

object ObjCTest extends TestSuite {

  val tests = Tests{
    'NSObject-{
      val obj = NSObject.alloc().init()
      fromCString( class_getName( obj.`class` ) ) ==> "NSObject"
    }
  }


  @ObjC
  class NSObject {
    @inline def `class`: id = extern
    @inline def hash: UInt = extern
    @inline def init(): this.type = extern
  }

  @ObjCClass
  abstract class NSObjectClass {
    type InstanceType
    def __cls: id
    def alloc(): InstanceType = extern
  }

  object NSObject extends NSObjectClass {
    override type InstanceType = NSObject
  }

}
