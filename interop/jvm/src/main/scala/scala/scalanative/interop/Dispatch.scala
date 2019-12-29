package scala.scalanative.interop

import com.sun.jna.{Callback, Library, Native, Pointer}

object Dispatch {

  trait DispatchFunction extends Callback {
    def invoke(f: Pointer): Unit
  }

  object DispatchFunction {
    private final class Impl(f: ()=>Unit) extends DispatchFunction {
      override def invoke(p: Pointer): Unit = f()
    }
    def fromFunction(f: ()=>Unit): DispatchFunction = new Impl(f)
  }

  private trait Helper extends Library {
    def swog_call_on_main_thread(f: DispatchFunction, block: Boolean): Unit
  }

  private lazy val __helper = Native.load("swog",classOf[Helper])

  @inline final def onMainThread(f: DispatchFunction, blocking: Boolean): Unit = __helper.swog_call_on_main_thread(f,blocking)

  /**
   * Executes the provided callback on the main thread (if supported by the underlying platform).
   *
   * @param f Callback to be executed
   * @param blocking If true, the callback is synchronously on the main thread.
   */
  @inline final def onMainThread(f: ()=>Unit, blocking: Boolean = false): Unit = __helper.swog_call_on_main_thread(DispatchFunction.fromFunction(f),blocking)
}
