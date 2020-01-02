package scala.scalanative.interop

import com.sun.jna._

import scala.collection.JavaConverters._
import scala.scalanative.unsafe.Ptr

object JNA {

  lazy val nativeIntSize: Int = Native.getNativeSize(classOf[Int])
  lazy val nativeCharSize: Int = Native.getNativeSize(classOf[Char])

  lazy val defaultMapper: TypeMapper = {
    val m = new DefaultTypeMapper()
    m.addTypeConverter(classOf[Ptr[_]],Ptr.PtrConverter)
    m
  }

  private lazy val options = Map(Library.OPTION_TYPE_MAPPER->defaultMapper).asJava

//  @inline final def ptrToCString(p: Ptr[Byte]): CString = p.asInstanceOf[CString]

  private var _nameResolver = JNANameResolver.interfaceName
  def nameResolver: JNANameResolver = _nameResolver
  def nameResolver_=(r: JNANameResolver): Unit = this.synchronized( _nameResolver = r )

  def loadInterface[T<:Library](fullName: String, iface: Class[T]): T =
    _nameResolver(fullName) match {
      case Some(libName) =>
        Native.load(libName,iface,options)
      case x => throw new RuntimeException(s"cannot resolve JNA library name for interface type '$x'")
    }

  def loadNativeLibrary(fullName: String): NativeLibrary =
    _nameResolver(fullName) match {
      case Some(libName) =>
        NativeLibrary.getInstance(libName,options)
      case x => throw new RuntimeException(s"cannot resolve JNA library name for interface type '$x'")
    }

  def loadGlobalPtr[T](libName: String, symbol: String): Ptr[T] =
    SWOGHelper.toPtr(loadNativeLibrary(libName).getGlobalVariableAddress(symbol))
}
