import de.surfice.smacrotools.debug

import scalanative._
import unsafe._
import objc._

object Main {
  def main(args: Array[String]): Unit = {
    val cstr = c"foo"
    val s = NSString.alloc().initWithUTF8String_(cstr)

    ext.NSLog(s.__ptr)
//    println(fromCString(Bar.bar(c"Hello")))
  }

}


