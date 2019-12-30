package scala.scalanative.interop

import com.sun.jna._

import scala.collection.JavaConverters._
import scala.scalanative.interop.jvm.JNANameResolver
import scala.scalanative.unsafe.Ptr

object JNA {

  lazy val nativeIntSize: Int = Native.getNativeSize(classOf[Int])
  lazy val nativeCharSize: Int = Native.getNativeSize(classOf[Char])

  lazy val mapper: TypeMapper = {
    val m = new DefaultTypeMapper()
    m.addTypeConverter(classOf[Ptr[_]],Ptr.PtrConverter)
    m
  }

  private lazy val options = Map(Library.OPTION_TYPE_MAPPER->mapper).asJava

//  @inline final def ptrToCString(p: Ptr[Byte]): CString = p.asInstanceOf[CString]

  private var _jnaNameResolver = JNANameResolver.interfaceName
  def jnaNameResolver: JNANameResolver = _jnaNameResolver
  def jnaNameResolver_=(r: JNANameResolver): Unit = this.synchronized( _jnaNameResolver = r )

  def loadJNALibrary[T<:Library](fullName: String, iface: Class[T]): T =
    _jnaNameResolver(fullName) match {
      case Some(libName) =>
        Native.load(libName,iface,options)
      case x => throw new RuntimeException(s"cannot resolve JNA library name for interface type '$x'")
    }

  def loadNativeLibrary(fullName: String): NativeLibrary =
    _jnaNameResolver(fullName) match {
      case Some(libName) =>
        NativeLibrary.getInstance(libName,options)
      case x => throw new RuntimeException(s"cannot resolve JNA library name for interface type '$x'")
    }

  def loadGlobalPtr[T](libName: String, symbol: String): Ptr[T] =
    SWOGHelper.toPtr(loadNativeLibrary(libName).getGlobalVariableAddress(symbol))
}
