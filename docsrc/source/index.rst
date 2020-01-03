.. swog documentation master file, created by
   sphinx-quickstart on Mon Jul 15 17:37:39 2019.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

swog - Scala Wrapper Object Generator
*******************************************
swog provides seamless integration of `Scala Native <https://www.scala-native.org>`_ with external, object-oriented libaries
written in **C**, **C++**, or **Objective-C**. As a highly experimental feature, bindings for C libraries can also be used
unmodified on the JVM (e.g. for prototyping with ammonite).

To this end it takes a plain Scala class/object and transforms it into a wrapper object during compilation.
This wrapper holds the pointer to the underlying external object, and converts arguments and return values of external calls
transparently to/from Scala types.

.. note::

  This guide is work in progress.

Features
========
- Use Scala classes for bindings to object-oriented C libraries (e.g. Gtk+), Objective-C classes, or C++ classes.

- Represent external type hierarchies with Scala classes and traits.

- Traits/classes/objects representing external objects can contain additional members defined in Scala.

- Scala-style camel case names for bindings can be automatically translated into C-style snake case
  (or other naming conventions like PascalCase); supports global prefix for bindings classes/objects.

- Map untyped collections returned by C or Objective-C bindings to Scala generic classes; interact with C++ template classes.

- Use bindings unmodified with ScalaNative and Scala/JVM (experimental, currently only supported for C).

- Integrate with embedded scripting languages (currently: **Lua**).


Example for C
=============

Suppose we have the following C struct + functions to operate on fractional numbers:

.. code-block:: c

  typedef struct {
    // numerator
    int num;
    // denominator
    int denom;
  } Fraction;

  /* constructor */
  Fraction* fraction_new(int num, int denom) { /* ... */ }
  
  /* property accessors */
  int fraction_get_numerator(Fraction* this) { return this->num; }
  void fraction_set_numerator(Fraction* this, int num) { this->num = num; }
  // ...
  
  /* multiply with another fraction */
  void fraction_multiply(Fraction* this, Fraction* that) { /* ... */ }

  // use it:
  Fraction* f = fraction_new(2,3);
  Fraction* g = fraction_new(6,5);
  fraction_multiply_with(f,g);

Using swog we can use this C type from Scala simply by declaring it as a class annotated with ``@CObj``:

.. code-block:: scala

  import scalanative._
  import unsafe._
  import cobj._

  @CObj
  class Fraction {
    def getNumerator(): Int = extern
    def setNumerator(num: Int): Unit = extern

    def getDenominator(): Int = extern
    def setDenominator(denom: Int): Int = extern

    def multiply(that: Fraction): Unit = extern
  }
  object Fraction {
    // bind to the 'constructor'
    @name("fraction_new")
    def apply(numerator: Int, denominator: Int): Fraction = extern
  }

  // use it:
  val f = Fraction(2,3)
  val g = Fraction(6,5)
  f.multiply(g)

  f.getNumerator() // returns 4



.. toctree::
   :maxdepth: 2
   :caption: Native Bindings:

   start
   CObj Bindings <cobj>
   C++ Bindings <cxx>
   Objective-C Bindings <objc>

.. toctree::
   :maxdepth: 2
   :caption: Scripting:

   Lua <lua>

.. toctree::
   :maxdepth: 2
   :caption: Examples:

   Gtk+ <example_gtk>
   Qt5 <example_qt>
   Cocoa <example_cocoa>



