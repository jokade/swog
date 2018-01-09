import de.surfice.smacrotools.debug

import scalanative.native._

object Main {
  def main(args: Array[String]): Unit = {
    val keys = new GKeyFile()
    val s = keys.toData(null,null)
    println(fromCString(s))
  }
}

@CObj
@debug
class GKeyFile() {
  def toData(length: Ptr[UInt], error: Ptr[Byte]): CString = extern

}
