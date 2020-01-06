package test.cxxlib

import cxxlib.CxxString
import utest._

import scala.scalanative._
import scala.scalanative.libc.string
import scala.scalanative.unsafe._

object CxxStringTest extends TestSuite {
  val tests = Tests {
    'constructor-{
      'empty-{
        val s = CxxString()
        s.length ==> 0
        string.strcmp(s.c_str,c"") ==> 0
        s.free()
      }
      'fromCString-{
        val s = CxxString(c"hello")
        s.length ==> 5
        string.strcmp(s.c_str,c"hello") ==> 0
        s.free()
      }
    }
    'push_back-{
      val s = CxxString()
      s.length ==> 0
      s.push_back('a'.toByte)
      s.length ==> 1
      string.strcmp(s.c_str,c"a") ==> 0
      s.free()
    }
    'append-{
      val s = CxxString(c"Hello")
      val q = s.append(c" World!")
      q.eq(s) ==> true
      s.length ==> 12
      string.strcmp(s.c_str,c"Hello World!") ==> 0
      s.free()
    }
    'clear-{
      val s = CxxString(c"Hello")
      string.strcmp(s.c_str,c"Hello") ==> 0
      s.clear()
      s.length ==> 0
      s.free()
    }
  }
}
