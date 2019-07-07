package scala.scalanative.interop

trait AutoReleasable extends Releasable {
  def autorelease(implicit pool: AutoreleasePool): this.type = {
    pool.register(this)
    this
  }
}
