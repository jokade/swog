package scala.scalanative.interop

import scala.scalanative.runtime.{Intrinsics, RawPtr}
import scala.scalanative.unsafe.Ptr

trait RefZone {
  /**
   * Holds a reference to a Scala object and returns the raw pointer to it.
   *
   * @param o
   */
  def export(o: AnyRef): RawPtr

  /**
   * Holds a reference to the specified Scala object and returns it.
   */
  def hold[T<:AnyRef](o: T): T = {
    export(o)
    o
  }

  /**
   * Releases the reference to the object (i.e. it may be collected)
   *
   * @param o
   */
  def release(o: AnyRef): Unit

  /**
   * Closes the zone and releases all references to exported objects.
   */
//  def close(): Unit
}

object RefZone {

  final class Impl extends RefZone {
    private val _set = collection.mutable.HashSet.empty[AnyRef]

    override def export(o: AnyRef): RawPtr = {
      _set += o
      Intrinsics.castObjectToRawPtr(o)
    }

//    override def close(): Unit = {}
    override def release(o: AnyRef): Unit = _set -= o
  }

  /**
   * A RefZone that doesn't retain references to exported objects.
   * Use only if you're sure, that the exported objects stay reachable after they were exported.
   */
  lazy val None = new RefZone {
    override def export(o: AnyRef): RawPtr = scalanative.runtime.Intrinsics.castObjectToRawPtr(o)

    override def release(o: AnyRef): Unit = {}
  }

  /**
   * A global RefZone that is never closed.
   * Warning: the objects exported with this zone will never be released!
   */
  lazy val Global = new Impl

  def apply[R](block: RefZone=>R): R = {
    val zone = new Impl
    block(zone)
  }

  object Implicits {
    implicit val None = RefZone.None
    implicit val Global = RefZone.Global
  }
}
