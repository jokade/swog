package scala.scalanative.unsafe

import scala.annotation.StaticAnnotation

/**
 * Provided for compatibility with platformJVM macro annotation.
 * Has no effect in a ScalaNative project.
 */
final class external(libname: String = null) extends StaticAnnotation

