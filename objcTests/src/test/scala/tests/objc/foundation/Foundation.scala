package tests.objc.foundation

import scala.scalanative.native.{CStruct0, CVararg, Ptr, extern}

@extern
object Foundation {

  def NSLog(format: NSString, args: CVararg*): Unit = extern
}
