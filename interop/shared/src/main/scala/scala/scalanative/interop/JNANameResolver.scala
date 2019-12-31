package scala.scalanative.interop

trait JNANameResolver {
  def apply(interfaceName: String): Option[String]
}
object JNANameResolver {
  def prefixResolver(prefixMappings: Seq[(String,String)]): JNANameResolver = new JNANameResolver {
    def apply(interfaceName: String): Option[String] =
      prefixMappings 
        .find(p => interfaceName.startsWith(p._1))
        .map(_._2)
  }

  def singleLibrary(lib: String): JNANameResolver = new JNANameResolver {
    def apply(interfaceName: String): Option[String] = Some(lib)
  }
  
  val interfaceName: JNANameResolver = new JNANameResolver {
    override def apply(interfaceName: String): Option[String] = Some(interfaceName)
  }
}

