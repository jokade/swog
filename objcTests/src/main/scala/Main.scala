import de.surfice.smacrotools.debug

import scalanative._
import unsafe._
import objc._
import scala.scalanative.annotation.InlineSource
import scala.scalanative.runtime.RawPtr

@InlineSource("C","void* foo(void* f) { return f; }")
object Main {
  def main(args: Array[String]): Unit = {
    println(Foo.foo(42))
    println(fromCString(Foo.bar(c"Hello")))
//    println(fromCString(Bar.bar(c"Hello")))
  }

}

@extern
object Foo {
  def foo(i: Int): Int  = extern
  @name("foo")
  def bar(s: CString): CString = extern
}

@extern
object Bar {
  @name("foo")
  def bar(s: CString): CString = extern
}
