package scala.scalanative.interop

package object jvm {

  private var _jnaNameResolver = JNANameResolver.interfaceName
  def jnaNameResolver: JNANameResolver = _jnaNameResolver
  def jnaNameResolver_=(r: JNANameResolver): Unit = this.synchronized( _jnaNameResolver = r )
}
