package scala.scalanative.native

trait AutoReleasable extends Releasable {
  def autorelease(implicit pool: AutoreleasePool): this.type = {
    pool.register(this)
    this
  }
}
