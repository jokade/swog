import de.surfice.smacrotools.debug

import scalanative.native._

object Main {
  def main(args: Array[String]): Unit = {

    Gtk.init(0,null)

    val win = new GtkWindow(0)
    win.setTitle(c"Hello Scala Native")
    win.setBorderWidth(10)
    win.setSizeRequest(200,100)

    val lbl = new GtkLabel(null)
    lbl.setMarkup(c"<span size='large'>Hello Scala Native!</span>")

    win.add(lbl)

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
class GtkWindow(tpe: Int) extends GtkContainer {
  final def setTitle(title: CString): Unit = extern
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
  final def setSizeRequest(width: Int, height: Int): Unit = extern
  final def showAll(): Unit = extern
}

@CObj(prefix = "g_signal_")
//@debug
trait GSignalReceiver {
  final def connectData(detailed_signal: CString, c_handler: CFunctionPtr0[Unit],
                        data: Ptr[Byte], destroy_data: Ptr[Byte], connect_flags: Int): Unit = extern
}

@CObj
@debug
trait GtkContainer extends GtkWidget {
  final def setBorderWidth(width: Int): Unit = extern
  final def add(widget: GtkWidget): Unit = extern
}

@CObj
//@debug
class GtkLabel(msg: CString) extends GtkWidget {
  final def setSelectable(flag: Boolean): Unit = extern
  final def setMarkup(str: CString): Unit = extern
}

@CObj
class GtkButton extends GtkWidget {
}
