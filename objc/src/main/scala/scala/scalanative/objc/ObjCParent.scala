// Copyright (c) 2017. Distributed under the MIT License (see included LICENSE file).
package scala.scalanative.objc

import scalanative.unsafe._
import runtime._
import helper._

object ObjCParent {
  private lazy val sel_initWithRef = sel_registerName(c"initWithRef:")
  private lazy val sel_methodSignatureForSelector = sel_registerName(c"methodSignatureForSelector:")
  private lazy val sel_forwardInvocation = sel_registerName(c"forwardInvocation:")

  private lazy val cls = {
    def init(self: id, sel: SEL, ref: id): id = {
      val proxy = objc_msgSend(self,sel_alloc)
      setScalaInstanceIVar(proxy,ref)
      println("Initializing Proxy!")
      proxy
    }
    /*
    def methodSignature(self: id, sel: SEL, selForSignature: SEL): id = {
      val ref = getScalaInstanceIVar[Object](self).asInstanceOf[id]
      objc_msgSend(ref,sel_methodSignatureForSelector,selForSignature)
    }
     */
    def forwardInvocation(self: id, sel: SEL, invocation: id): id = {
      val ref = getScalaInstanceIVar[Object](self).asInstanceOf[id]
      println("forwarding invocation")
      self
    }
    val newCls = objc_allocateClassPair(objc_getClass(c"NSProxy"),c"ScalaParentProxy",0)
    val metaCls = object_getClass(newCls)
    addScalaInstanceIVar(newCls)
    /* TODO:
    class_addMethod(metaCls,sel_initWithRef,CFuncPtr.fromFunction3(init),c"@:@")
    class_addMethod(newCls,sel_methodSignatureForSelector,CFuncPtr.fromFunction3(methodSignature),c"@:@")
    class_addMethod(newCls,sel_forwardInvocation,CFuncPtr.fromFunction3(forwardInvocation),c"@:@")

     */
    objc_registerClassPair(newCls)
    newCls
  }
/*
  def apply(ref: id): id = {
    val proxy = objc_msgSend(cls,sel_initWithRef,ref)
    proxy
  }

 */
}
