package scala.scalanative.cxx

import scala.annotation.StaticAnnotation

class constructor(classname: String = null) extends StaticAnnotation

class include(header: String) extends StaticAnnotation

class delete extends StaticAnnotation

/**
 * Indicates that the annotated method returns a `const` value.
 */
class returnsConst extends StaticAnnotation

/**
 * Indicates that the annotated method returns a reference instead of a pointer.
 */
class returnsRef extends StaticAnnotation

class returnsValue extends StaticAnnotation

class ref extends StaticAnnotation

class cxxName(name: String) extends StaticAnnotation