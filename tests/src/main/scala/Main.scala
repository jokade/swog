import de.surfice.smacrotools.debug

import scalanative.native._

object Main {
  def main(args: Array[String]): Unit = {
    Gtk.init(0,null)
    val win = new GtkWindow(0.toUInt)

    win.setTitle(c"Hello world!")
    win.setBorderWidth(100)

    win.connectData(c"destroy",CFunctionPtr.fromFunction0(destroy),null,null,0)

    win.showAll()

    Gtk.main()
  }

  def destroy(): Unit = {
    println("exiting...")
    Gtk.main_quit()
  }
}

@CObj
//@debug
class GtkWindow(tpe: UInt) extends GtkContainer {
  def setTitle(title: CString): Unit = extern
}

@CObj
//@debug
object Gtk {
  def init(argc: Int, argv: Ptr[CString]): Unit = extern
  def main(): Unit = extern
  def main_quit(): Unit = extern
}

@CObj
//@debug
trait GtkWidget extends GSignalReceiver {
  final def showAll(): Unit = extern
}

@CObj(prefix = "g_signal_")
@debug
trait GSignalReceiver {
  final def connectData(detailed_signal: CString, c_handler: CFunctionPtr0[Unit],
                        data: Ptr[Byte], destroy_data: Ptr[Byte], connect_flags: Int): Unit = extern
}

@CObj
trait GtkContainer extends GtkWidget {
  final def setBorderWidth(width: Int): Unit = extern
}