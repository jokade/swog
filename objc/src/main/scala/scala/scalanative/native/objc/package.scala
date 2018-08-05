package scala.scalanative.native

import scala.annotation.StaticAnnotation
import scala.scalanative.native.objc.runtime.{ClassPtr, id}

package object objc {

  val objc_nil: id = null

  val objc_Nil: ClassPtr = null

  var defaultRootObject: ClassPtr = runtime.objc_getClass(c"NSObject")

}
