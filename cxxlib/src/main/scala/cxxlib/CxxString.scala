package cxxlib

import de.surfice.smacrotools.debug

import scalanative._
import unsafe._
import cxx._
import cobj._
import scala.scalanative.annotation.alwaysinline
import scala.scalanative.interop.AutoReleasable

/**
 * Wrapper for std::string.
 *
 * @see [[http://www.cplusplus.com/reference/string/string/]]
 */
@Cxx(namespace = "std", classname = "string")
@include("<string>")
@debug
class CxxString extends CxxObject with AutoReleasable {
  final def length: Int = extern

  @returnsConst
  final def c_str: CString = extern

  final def push_back(c: CChar): Unit = extern
  @inline final def push_back(c: Char): Unit = push_back(c.toByte)


  @returnsThis
  @returnsRef
  def append(s: CString): CxxString = extern

  def clear(): Unit = extern

//  @name("cxxlib_CxxString_$colon$eq_1820053044")
//  @cxxBody("__p->operator=(c_str);")
//  def :=(c_str: CString): Unit = extern

  @delete
  override def free(): Unit = extern
}

object CxxString {
  @constructor
  def apply(): CxxString = extern
  @constructor
  def apply(c_str: CString): CxxString = extern

//  @alwaysinline def stackalloc(c_str: CString): CxxString = {
//    val p = unsafe.stackalloc[Byte](__sizeof)
//    val s = new CxxString(p)
//    s := c_str
//    s
//  }
}
