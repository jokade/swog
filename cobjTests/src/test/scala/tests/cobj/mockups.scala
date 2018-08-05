package tests.cobj

import scalanative.native._
import CObj.implicits._

@CObj
// TODO: add args: CVararg*
class GError(_domain: Int, _code: Int, _fmt: CString)
  extends CObj.CRef[CStruct3[Int,Int,CString]] {
  def domain: Int = !__ref.toPtr._1
  def code: Int = !__ref.toPtr._2
  def msg: CString = !__ref.toPtr._3
  def free(): Unit = extern
}