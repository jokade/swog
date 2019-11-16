package scala.scalanative.interop.platform.macos

import com.sun.jna.{Callback, Library, Native, NativeLibrary, Pointer}

object Dispatch {

  final class Queue(q: Pointer) {
    def async(f: ()=>Unit): DispatchFunction = {
      val df = new DispatchFunction {
        override def invoke(ctx: Pointer): Unit = f()
      }
      __ext.dispatch_async_f(q, null,df)
      df
    }
      
    
    def asyncAndWait(f: ()=>Unit): Unit = __ext.dispatch_async_and_wait_f(q,null, new DispatchFunction {
      override def invoke(ctx: Pointer): Unit = f()
    })
    
    def sync(f: ()=>Unit): Unit = __ext.dispatch_sync_f(q,null, new DispatchFunction {
      override def invoke(ctx: Pointer): Unit = f()
    })
    def iter(n: Long, f: ()=>Unit): DispatchFunction = {
      val df = new DispatchFunction {
        override def invoke(ctx: Pointer): Unit = f()
      }
      __ext.dispatch_apply_f(n,q,null,df)
      df
    }
  }
  
  lazy val mainQueue: Queue = {
    val q = NativeLibrary.getInstance("System").getGlobalVariableAddress("_dispatch_main_q")
    new Queue(q)
  }
  
  trait DispatchFunction extends Callback {
    def invoke(f: Pointer): Unit
  }
  
  private trait IFace extends Library {
    def dispatch_async_f(queue: Pointer, ctx: Pointer, f: DispatchFunction): Unit
    def dispatch_async_and_wait_f(queue: Pointer, ctx: Pointer, f: DispatchFunction): Unit
    def dispatch_sync_f(queue: Pointer, ctx: Pointer, f: DispatchFunction): Unit
    def dispatch_apply_f(n: Long, queue: Pointer, ctx: Pointer, f: DispatchFunction)
  }
  
  private lazy val __ext = Native.load("System",classOf[IFace])
}
