package scala.scalanative.interop


object Dispatch {
  /**
   * Executes the provided callback on the main thread (if supported by the underlying platform).
   *
   * @param f Callback to be executed
   * @param blocking If true, the callback is synchronously on the main thread.
   */
    // TODO: implement callback on main thread for SN (currently we assume we're already on the main thread and just call f)
  @inline final def onMainThread(f: ()=>Unit, blocking: Boolean = false): Unit = f()
}
