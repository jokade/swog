//     Project: scalanative-cocoa
//      Module: objc
// Description: Helper functions for interaction with the ObjC runtime

// Copyright (c) 2017. Distributed under the MIT License (see included LICENSE file).
package scala.scalanative.objc

import runtime._
import scala.scalanative.annotation.alwaysinline
import scala.scalanative.cobj.CObjectWrapper
import scala.scalanative.libc.stdlib.malloc
import scala.scalanative.runtime.RawPtr
import scalanative._
import unsafe._
import unsigned._

object helper {

//  implicit def objectToId(o: ObjCObject): id = o.asInstanceOf[id]
  lazy private val nsObjectCls = objc_lookUpClass(c"NSObject")

  private val scalaInstanceIVar = c"s"
  val sel_alloc: SEL = sel_registerName(c"alloc")
  val sel_allocWithZone: SEL = sel_registerName(c"allocWithZone:")

  @alwaysinline private def prepareSendSuper(self: id, op: SEL, obj: Ptr[objc_super]) = {
    val super_class = class_getSuperclass(object_getClass(self.asInstanceOf[Ptr[Byte]]))
    obj._1 = self
    obj._2 = super_class
  }

  def msgSendSuper0(self: id, op: SEL): id = {
    val objc_super = stackalloc[objc_super]( sizeof[objc_super] )
    prepareSendSuper(self,op,objc_super)
    runtime.objc_msgSendSuper(objc_super,op)
  }

  def msgSendSuper1(self: id, op: SEL, arg1: Ptr[Byte]): id = {
    val objc_super = stackalloc[objc_super]( sizeof[objc_super] )
    prepareSendSuper(self,op,objc_super)
    runtime.objc_msgSendSuper(objc_super,op, arg1)
  }

  def allocWithZone(zone: id, isa: ClassPtr): id = {
    val obj = objc_msgSend(nsObjectCls, sel_allocWithZone, zone).asInstanceOf[Ptr[CStruct1[ClassPtr]]]
    obj._1 = isa
    obj.asInstanceOf[id]
  }
  
  @inline def addScalaInstanceIVar(cls: ClassPtr): Boolean = runtime.class_addIvar(cls,scalaInstanceIVar,8,3.toUByte,null)

  @inline def setScalaInstanceIVar(obj: id, instance: Object): runtime.IVar =
    runtime.object_setInstanceVariable(obj,scalaInstanceIVar,interop.objectToPtr(instance))

  // TODO: can we use IVar instead of getInstanceVariable?
  @inline def getScalaInstanceIVar[T](obj: id): T = {
    val out = stackalloc[Ptr[Byte]]( sizeof[Ptr[Byte]] )
    runtime.object_getInstanceVariable(obj,scalaInstanceIVar,out)
    interop.rawPtrToObject((!out).rawptr).asInstanceOf[T]
  }

  /*
    def allocProxy(clsObj: id, instance: id=>Object): id = {
      val obj = msgSendSuper0(clsObj,sel_alloc)
      setScalaInstanceIVar(obj,instance(obj))
      obj
    }
  */
//  implicit final class RichObjCObject(val o: ObjCObject) extends AnyVal {
    // due to https://github.com/scala-native/scala-native/issues/547 we currently can't pass on CVararg*
    // so we explicitly provide methods for multiple arg counts
//    def msgSend(selector: CString, arg1: CVararg): id = objc_msgSend(o,sel_registerName(selector),arg1)
//    def msgSend(selector: CString, arg1: CVararg, arg2: CVararg): id = objc_msgSend(o,sel_registerName(selector),arg1,arg2)
//  }

}



