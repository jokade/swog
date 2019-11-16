package scala.scalanative.unsafe

import com.sun.jna.{Pointer, SWOGHelper}

import scala.annotation.implicitNotFound

@implicitNotFound("Given method requires an implicit zone.")
trait Zone {
  /* Interface defined by ScalaNative */
  def alloc(size : scala.scalanative.unsafe.CSize) : scala.scalanative.unsafe.Ptr[scala.Byte]
  def close() : scala.Unit
  def isOpen : scala.Boolean
  def isClosed : scala.Boolean

  /* additional methods used only on JVM */
  def makeNativeString(s: String): CString
}
object Zone {

  protected[scalanative] class ZoneImpl extends Zone {
    private var _closed = false
    private val _refs = collection.mutable.UnrolledBuffer.empty[Ptr[_]]

    override def alloc(size: CSize): Ptr[Byte] =
      if(_closed)
        throw new RuntimeException("Zone is already closed!")
      else {
        ???
      }

    override def close(): Unit = synchronized{
      _refs.clear()
      _closed = false
    }
    override def isOpen: CBool = !_closed
    override def isClosed: CBool = _closed

    override def makeNativeString(s: String): CString =
      if(_closed)
        throw new RuntimeException("Zone is already closed!")
      else {
        val ns = SWOGHelper.nativeString(s)
        _refs += ns
        ns
      }
  }

  protected[scalanative] lazy val global: Zone = new ZoneImpl

  final def apply[T](f: Zone => T): T = {
    f(global)
  }
}
