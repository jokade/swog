package scala.scalanative.interop

trait Retainable extends Releasable {
  def retain(): this.type
}

trait AutoRetainable extends Retainable with AutoReleasable {
  override def autorelease(implicit pool: AutoreleasePool): AutoRetainable.this.type = {
    retain()
    super.autorelease
  }
}
