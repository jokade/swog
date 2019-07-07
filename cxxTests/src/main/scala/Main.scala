import de.surfice.smacrotools.debug

import scalanative._
import unsafe._
import scala.scalanative.annotation.ExternalSource
import scala.scalanative.cobj.CObj
import scala.scalanative.cxx.{Cxx, constructor}

/*
object Main {
  def main(args: Array[String]): Unit = {
    val foo = Foo(3)
    println(foo.get(2))
//    println(base.bar.get())
  }
}

@Cxx
@debug
@ExternalSource("Cxx",
"""
class Foo {
    int num;
  public:
    Foo(int n);
    int get(int i);
};
Foo::Foo(int n) {
  num = n;
}
int Foo::get(int i) { return num + i; }
""")
class Foo {
  def get(i: Int): Int = extern
}

object Foo {
  @constructor("Bar")
  def apply(num: Int): Foo = extern

}
*/
/*
package base {
  @Cxx
  @debug
  @ExternalSource("Cxx",
"""
namespace base::bar {
  int get() { return 123; }
}""")
  object bar {
    def get(): Int = extern
  }
}
*/
