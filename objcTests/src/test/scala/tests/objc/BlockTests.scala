package tests.objc

import tests.objc.foundation._
import utest._
import scalanative.native._
import objc._

object BlockTests extends TestSuite {


  val tests = Tests {
    'sortStrings{
      val strings = NSArray(
        NSString("string 1"),
        NSString("String 21"),
        NSString("string 12"),
        NSString("String 11"),
        NSString("String 02")
      )
      'withZoneAlloc- {
        BlockZone { implicit z =>
//          implicit val ba = BlockAlloc.zone
          val sorted = sortStrings(strings)
          sorted(0).string ==> "String 02"
          sorted(1).string ==> "String 11"
          sorted(2).string ==> "string 12"
          sorted(3).string ==> "String 21"
          sorted(4).string ==> "string 1"
        }
      }
      'withGlobalAlloc-{
        import BlockAlloc.Implicits.global
        val sorted = sortStrings(strings)
        sorted(0).string ==> "String 02"
        sorted(1).string ==> "String 11"
        sorted(2).string ==> "string 12"
        sorted(3).string ==> "String 21"
        sorted(4).string ==> "string 1"
      }
    }
  }

  def sortStrings(array: NSArray[NSString])(implicit ba: BlockAlloc): NSArray[NSString] =
    array.sortedArrayUsingComparator_( (block:Block,a:NSString,b:NSString) => a.string.compare(b.string) )
}
