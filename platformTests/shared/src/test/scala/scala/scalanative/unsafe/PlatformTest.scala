package scala.scalanative.unsafe

import utest._

//import scala.scalanative.{LibC, Mockups}

object PlatformTest extends TestSuite {
  val tests = Tests{
/*
    'CString-{
      'global-{
        val s1 = c"Hello"

        LibC.strlen(s1) ==> 5

        val s2 = Mockups.returnString(s1)
        fromCString(s2) ==> "Hello"
      }
      'dynamic-{
        Zone{ implicit z =>
          val s1 = toCString("foobar")
          LibC.strlen(s1) ==> 6

          System.gc()
          LibC.strlen(s1) ==> 6

          val s2 = Mockups.returnString(s1)
          System.gc()
          fromCString(s2) ==> "foobar"
        }
      }

      'Struct-{
//        val s = Mockups.getStruct()
//        println(!s._1)
      }
    }
    */
  }
}
