package scala.scalanative

import scala.language.experimental.macros

package object cobj {

  /**
   * Returns the default wrapper factory for the specified type.
   *
   * The default wrapper factory creates a new wrapping instance for every call to `wrap()`.
   *
   * @tparam T
   * @return
   */
  def defaultWrapper[T<:CObject]: CObjectWrapper[T] = macro Macros.defaultWrapperImpl[T]

  /**
   * Returns a singleton wrapper factory for the specified type.
   *
   * The returned factory re-uses a single object to wrap the native object pointers.
   *
   * @tparam T
   * @return
   */
  def singletonWrapper[T<:MutableCObject]: CObjectWrapper[T] = macro Macros.singletonWrapperImpl[T]
}
