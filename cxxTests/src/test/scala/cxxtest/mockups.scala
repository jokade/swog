package cxxtest

import scala.scalanative.annotation.ExternalSource
import scala.scalanative.cxx.CxxEnum

@ExternalSource("Cxx",
"""
enum FooEnum {
   Foo = 1,
   Bar = 2
};

class TestEnum {
  FooEnum e;
public:
  TestEnum(FooEnum en) : e{en} { }
  FooEnum get() { return e; }
};
""")
object mockups {
  def loadCxx() = {}
}

object FooEnum extends CxxEnum {
  val Foo = Value(1)
  val Bar = Value(2)
}
