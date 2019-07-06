package scala.scalanative.cxx

import scala.scalanative.cobj.CEnum

abstract class CxxEnum extends CEnum {
  def cxxType: String = null
}


