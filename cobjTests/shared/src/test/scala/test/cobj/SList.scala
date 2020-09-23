package test.cobj

import scala.scalanative.cobj.{CObj, CObject, CObjectWrapper, nullable}
import scala.scalanative.unsafe.{extern, name}

@CObj(prefix = "slist_")
class SList[T] extends CObject {

  def isEmpty: Boolean = extern
  def size: Int = extern
  def prepend(value: T)(implicit wrapper: CObjectWrapper[T]): SList[T] = extern

  // returns null if the specified index does not exist
  @nullable
  def itemAt(index: Int)(implicit wrapper: CObjectWrapper[T]): T = extern

}

object SList {
  @name("slist_new")
  def apply[T<:CObject](): SList[T] = extern
}
