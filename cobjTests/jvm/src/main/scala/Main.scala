import com.sun.jna.{Callback, CallbackReference, CallbackThreadInitializer, Native, NativeLibrary, Pointer}
import de.surfice.smacrotools.debug

import scala.scalanative._
import unsafe._
import cobj._
import scala.scalanative.interop.{JNA, JNANameResolver}

@CObj
@debug
object Main {

  JNA.nameResolver = JNANameResolver.prefixResolver(Seq("Gtk"->"gtk-3"))

  @syncOnMainThread
  def main(args: Array[String]): Unit = {

//    Dispatch.onMainThread( () => {
      println("CALLED!")
      Gtk.init(0,null)
      val win = GtkWindow(0)
      win.setTitle("Foo")
      win.showAll()
      Gtk.main()
//    }, true)
  }
  
}

@CObj
object Gtk {
  def init(argc: Int, argv: Ptr[Byte]): Unit = extern
  def mainIterationDo(blocking: CBool): Unit = extern
  def mainIteration(): Unit = extern
  def main(): Unit = extern
  def eventsPending(): Boolean = extern

}

@CObj
class GtkWidget {
  def showAll(): Unit = extern
}

@CObj
class GtkWindow extends GtkWidget {
  def setTitle(s: String): Unit = extern
}
object GtkWindow {
  @name("gtk_window_new")
  def apply(tpe: Int): GtkWindow = extern
}
