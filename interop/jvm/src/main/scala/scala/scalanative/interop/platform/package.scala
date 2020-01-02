package scala.scalanative.interop

import com.sun.jna.{Library, Native}

import scala.scalanative.unsafe.{CString, Ptr}

package object platform {

  @inline final def ptrToCString(p: Ptr[Byte]): CString = p.asInstanceOf[CString]

  private var _jnaNameResolver = JNANameResolver.interfaceName
  def jnaNameResolver: JNANameResolver = _jnaNameResolver
  def jnaNameResolver_=(r: JNANameResolver): Unit = this.synchronized( _jnaNameResolver = r )

  def loadJNALibrary[T<:Library](fullName: String, iface: Class[T]): T =
    _jnaNameResolver(fullName) match {
      case Some(libName) =>
        Native.load(libName,iface)
      case x => throw new RuntimeException(s"cannot resolve JNA library name for interface type '$x'")
    }
}
