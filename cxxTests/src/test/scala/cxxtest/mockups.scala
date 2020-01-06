package cxxtest

import de.surfice.smacrotools.debug

import scala.scalanative.annotation.InlineSource
import scala.scalanative.cobj.{CEnum, ResultValue}
import scala.scalanative.cxx._
import scala.scalanative.unsafe._

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

@Cxx
class Date extends CxxObject {
  def day: Int = extern
  def month: Int = extern
  def year: Int = extern

  def addDays(d: Int): Int = extern

  def compare(other: Date): Int = extern

  @returnsValue
  def self()(implicit res: ResultValue[Date]): Unit = extern

  @cxxBody("return 42;")
  def foo(): Int = extern

  @delete
  def free(): Unit = extern
}

@InlineSource("Cxx",
"""
class Date {
  int d, m, y;
public:
  Date() : Date(1,1,1900) {
  }

  Date(int day, int month, int year) :
   d{day}, m{month}, y{year} {
  }

  int day() const { return d; }
  int month() const { return m; }
  int year() const { return y; }
  int hash() const { return d + 12*m + 366*y; }
  Date self() { return *this; }

  int addDays(int days) {
    d += days;
    return d;
  }

  int compare(Date* other) const {
    int thash = this->hash();
    int ohash = other->hash();
    if(thash < ohash) {
      return -1;
    }
    else if(thash > ohash) {
      return 1;
    }
    else {
      return 0;
    }
  }

  static Date value() { return Date(); }
};
""")
object Date extends CxxClass {
  @constructor
  def apply(): Date = extern

  @constructor
  def apply(day: Int, month: Int, year: Int): Date = extern

  @returnsValue
  def value()(implicit res: ResultValue[Date]): Unit = extern


  @cxxBody("return 43;")
  def bar(): Int = extern
}

@Cxx
class ImplicitConstructor()(implicit s: String) extends CxxObject {
  def string: String = s
  def self()(implicit s: String): ImplicitConstructor = extern
}
@InlineSource("Cxx",
"""
class ImplicitConstructor {
public:
  ImplicitConstructor* self() { return this; }
};
""")
object ImplicitConstructor {
  @constructor
  def apply()(implicit s: String): ImplicitConstructor = extern
}