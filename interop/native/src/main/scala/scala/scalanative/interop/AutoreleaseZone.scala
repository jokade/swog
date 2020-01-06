
package scala.scalanative.interop

import scala.scalanative.annotation.alwaysinline
import scala.scalanative.interop
import scala.scalanative.unsafe.{CSize, CString, Ptr, Zone}

class AutoreleaseZone private(zone: Zone) extends AutoreleasePool with Zone {
  @alwaysinline final def alloc(size: CSize): Ptr[Byte] = zone.alloc(size)
  def close(): Unit = {
    releaseAll()
    zone.close()
  }
  @alwaysinline final def isClosed: Boolean = zone.isClosed
  @alwaysinline final override def isOpen: Boolean = zone.isOpen
}
object AutoreleaseZone {
  def apply[R](block: AutoreleaseZone => R): R = {
    val zone = new interop.AutoreleaseZone(Zone.open())
    val res = block(zone)
    zone.close()
    res
  }
}

