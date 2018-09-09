package scala.scalanative.native.cobj

object NamingConvention extends Enumeration {
  val None = Value
  /**
   * Upper case letters within a method name are replaced with underscores followed by the
   * corresponding lower case letter, i.e. `fooBar` -> `foo_bar`
   */
  val SnakeCase = Value
  /**
   * The first letter of the method name is transformed to upper case, i.e. `fooBar` -> `FooBar`
   */
  val PascalCase = Value
}

