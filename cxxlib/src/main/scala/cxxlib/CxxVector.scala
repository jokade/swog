package cxxlib

import scala.scalanative._
import scala.scalanative.cxx._
import scala.scalanative.unsafe._

/**
 * Wrapper for std::vector.
 *
 * @see [[http://www.cplusplus.com/reference/vector/vector/]]
 *
 * @note You must 'instantiate' this trait explicitly for every type T.
 *       For example, if you want to use a `CxxVector[Int]`, you must explicitly
 *       create a class that extends `CxxVector[Int]` (see use example)
 * @example
 * ```
 * @Cxx
 * class CxxIntVector extends CxxVector[Int]
 * ```
 */
@CxxTemplate(namespace = "std", classname = "vector")
@include("<vector>")
trait CxxVector[T] {
  def size: CSize
  def resize(n: CSize): Unit
  def push_back(value: T): Unit
  @cxxBody("return __p->operator[](idx);")
  def apply(idx: CSize): T
  @cxxBody("__p->operator[](idx) = value;")
  def update(idx: CSize, value: T): Unit
}



