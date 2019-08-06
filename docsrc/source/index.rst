.. swog documentation master file, created by
   sphinx-quickstart on Mon Jul 15 17:37:39 2019.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

swog - ScalaNative Wrapper Object Generator
*******************************************
swog provides seamless integration of `Scala Native <https://www.scala-native.org>`_ with external, object-oriented libaries
written in **C**, **C++**, or **Objective-C**, as well as integration with embedded scripting languages (currently: **Lua**).
To this end it takes a plain Scala class/object and transforms it into a wrapper object that handles the interop with the
underlying external object under the hood.

.. note::

  This guide is work in progress.


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



