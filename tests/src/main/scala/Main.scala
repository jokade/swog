import de.surfice.smacrotools.debug

import scalanative.native._

object Main {
  def main(args: Array[String]): Unit = {
    Gtk.init(0,null)
    val win = new GtkWindow(0.toUInt)
//    win.setTitle(c"Hello world!")
  }
}

@CObj
@debug
class GtkWindow(tpe: UInt) {

  def setTitle(title: CString): Unit = extern
}
@CObj
@debug
object Gtk {
  def init(argc: Int, argv: Ptr[CString]): Unit = extern
}

