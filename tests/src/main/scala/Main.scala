import de.surfice.smacrotools.debug

import scalanative.native._

object Main {
  def main(args: Array[String]): Unit = {
    val list = new GList()
    println(list.length())
  }
}

@CObj(newSuffix = "alloc")
@debug
class GList private() {
  def length(): UInt = extern
}
object GList {
  def append()
}
