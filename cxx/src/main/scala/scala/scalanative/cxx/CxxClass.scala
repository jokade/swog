package scala.scalanative.cxx

trait CxxClass {
  self: Singleton =>

  /**
   * Returns the size of the underlying C++ type.
   */
  def __sizeof: Int
}
