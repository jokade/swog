import com.sun.jna.{Callback, NativeLibrary, Pointer}
import de.surfice.smacrotools.debug

import scala.scalanative._
import unsafe._
import cobj._
import scala.scalanative.interop.platform
import scala.scalanative.interop.platform.macos.Dispatch

object Main {

  def init(): Unit = {
    val win = GtkWindow(0)
    win.setTitle("Hello")
    win.showAll()
  }
  
  def run(): Unit = {
    Gtk.mainIterationDo(true)
  }
  
  def main(args: Array[String]): Unit = {
    import Dispatch.mainQueue
    
    platform.jnaNameResolver = platform.JNANameResolver.prefixResolver(Seq("Gtk"->"gtk-3"))
    Gtk.init(0,null)
    
    val i = mainQueue.async(init)

    val df = mainQueue.async( () => Gtk.main() )
    while(true) {}
    println(i)
    println(df)
    //    Gtk.main()
    println("done")
  }
  
}

@CObj
object Gtk {
  def init(argc: Int, argv: Ptr[Byte]): Unit = extern
  def mainIterationDo(blocking: CBool): Unit = extern
  def main(): Unit = extern
//  def main(): Unit = {
//    while(true) {
//      Dispatch.mainQueue.asyncAndWait{
//        mainIterationDo(false)
//      }
//    }
//  }
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
