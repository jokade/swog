package cxxtest

import de.surfice.smacrotools.debug
import utest._

import scala.scalanative._
import annotation.ExternalSource
import unsafe._
import cxx._

object ClassTest extends TestSuite {

  val tests = Tests {
    'constructor-{
      val date1 = Date()
      date1.day ==> 1
      date1.month ==> 1
      date1.year ==> 1900

      val date2 = Date(13,5,2019)
      date2.day ==> 13
      date2.month ==> 5
      date2.year ==> 2019

      date1.free()
      date2.free()
    }
    'methods-{
      val date1 = Date()
      date1.addDays(5) ==> 6
      date1.day ==> 6

      val date2 = Date(3,5,1978)
      date1.compare(date2) ==> -1
      date2.compare(date1) ==> 1
      date2.compare(date2) ==> 0

      date1.free()
      date2.free()
    }
  }
}


@Cxx
@debug
@ExternalSource("Cxx",
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
};
""")
class Date {
  def day: Int = extern
  def month: Int = extern
  def year: Int = extern

  def addDays(d: Int): Int = extern

  def compare(other: Date): Int = extern

  @delete
  def free(): Unit = extern
}

object Date {
  @constructor
  def apply(): Date = extern

  @constructor
  def apply(day: Int, month: Int, year: Int): Date = extern

}
