package tests.objc

import utest._
import scalanative.native._
import objc._
import runtime._
import reflect._

object ReflectionTests extends TestSuite {

  val tests = Tests{
    val cls = objc_allocateClassPair(objc_Nil,c"NewRootClass",0)
    val clsDesc = ClassDescriptor(cls)

    'methods-{
      val methods = clsDesc.methods()
      methods.size ==> 0
    }
  }
}
