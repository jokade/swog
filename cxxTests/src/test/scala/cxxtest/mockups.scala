package cxxtest

import de.surfice.smacrotools.debug

import scala.scalanative.annotation.InlineSource
import scala.scalanative.cobj.CEnum
import scala.scalanative.cxx.CxxEnum
import scala.scalanative.unsafe.name

@InlineSource("Cxx",
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

@CxxEnum("FooEnum")
object FooEnum extends CEnum {
  val Foo = Value(1)
  val Bar = Value(2)
}
