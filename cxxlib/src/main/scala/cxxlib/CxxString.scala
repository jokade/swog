package cxxlib

import de.surfice.smacrotools.debug

import scalanative._
import unsafe._
import cxx._
import cobj._
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

  @delete
  override def free(): Unit = extern
}

object CxxString {
  @constructor
  def apply(): CxxString = extern
  @constructor
  def apply(c_str: CString): CxxString = extern
}
