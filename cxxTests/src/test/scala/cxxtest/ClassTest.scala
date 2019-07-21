package cxxtest

import de.surfice.smacrotools.debug
import utest._

import scala.scalanative._
import annotation.InlineSource
import unsafe._
import cxx._
import scala.scalanative.cobj.{ResultPtr, ResultValue}

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
    'sizeof-{
      Date.__sizeof ==> 12
    }
    'returnsValue-{
      'static-{
        'stackalloc-{
          implicit val out = ResultValue.stackalloc[Date]
          Date.value()
          out.wrappedValue.year ==> 1900
        }
        'alloc-{ Zone { implicit z =>
          implicit val out = ResultValue.alloc[Date]
          Date.value()
          out.wrappedValue.year ==> 1900
        }}
      }
      'instance-{
        val d = Date()
        'stackalloc-{
          implicit val out = ResultValue.stackalloc[Date]
          d.self()
          out.wrappedValue.year ==> 1900
        }
        'alloc-{ Zone { implicit z =>
          implicit val out = ResultValue.alloc[Date]
          d.self()
          out.wrappedValue.year ==> 1900
        }}
      }
    }
    'cxxBody-{
      'method-{
        val d = Date()
        d.foo() ==> 42
      }
      'function-{
        Date.bar() ==> 43
      }
    }

    'implicitConstructor-{
      implicit val s = "implicit"
      val obj = ImplicitConstructor()
      obj.string ==> s
      obj.self().string ==> s
    }
  }
}



