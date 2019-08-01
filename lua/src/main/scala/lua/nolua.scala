package lua

import scala.annotation.StaticAnnotation

/**
 * A method annotated with this will not be exposed to Lua.
 */
class nolua extends StaticAnnotation
