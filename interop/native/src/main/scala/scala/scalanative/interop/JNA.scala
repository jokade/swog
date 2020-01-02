package scala.scalanative.interop

object JNA {

  private var _nameResolver = JNANameResolver.interfaceName
  def nameResolver: JNANameResolver = _nameResolver
  def nameResolver_=(r: JNANameResolver): Unit = this.synchronized( _nameResolver = r )
}
