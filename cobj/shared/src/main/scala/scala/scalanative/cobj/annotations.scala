package scala.scalanative.cobj

import scala.annotation.StaticAnnotation

class returnsThis extends StaticAnnotation

class updatesThis extends StaticAnnotation

class nullable extends StaticAnnotation

/**
 * If a method is annotated with this, it will be executed synchronoulsy (i.e. blocking) on the main thread
 * (if that concept exists for the current platform).
 */
class syncOnMainThread extends StaticAnnotation
