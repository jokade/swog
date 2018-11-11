package scala.scalanative.native

import scala.annotation.StaticAnnotation
import scala.scalanative.native.objc.runtime.{ClassPtr, ObjCObject, id}
import scala.language.experimental.macros

package object objc {

  var defaultRootObject: ClassPtr = runtime.objc_getClass(c"NSObject")

//  implicit def function2Block1[T1,R](f: (Block,T1) => R): Block1[T1,R] = macro Block.Macros.blockImpl
//  implicit def function2Block2[T1,T2,R](f: (Block,T1,T2) => R): Block2[T1,T2,R] = macro Block.Macros.blockImpl

//  def $super[T,R](self: T)(f: T=>R): R = macro Macros.superImpl

  def $superWithResult[T,R](self: id)(f: T=>R)(implicit wrapper: ObjCWrapper[T]): R = macro Macros.superWithResultImpl

  def $super[T](self:id)(f: T=>Any): Unit = macro Macros.superImpl

//  def wrapper[T<:ObjCObject](obj: T): ObjCWrapper[T] = new ObjCWrapper.Reuse[T](obj)
}
