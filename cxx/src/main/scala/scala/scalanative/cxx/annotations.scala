package scala.scalanative.cxx

import scala.annotation.StaticAnnotation

class constructor(classname: String = null) extends StaticAnnotation

class include(header: String) extends StaticAnnotation

class delete extends StaticAnnotation