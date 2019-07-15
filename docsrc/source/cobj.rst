CObj - Object-oriented C Bindings
*********************************

Introduction
============
The simplest form of object-oriented programming in C is based on a simple convention:
an object consists of a structure and a set of functions operating on that structure, e.g.:

.. code-block:: c

  typedef struct {
    int num;
    int denom;
  } Fraction;

  // creates a new Fraction instance
  Fraction* fraction_new(int num, int denom);

  // accessor for property 'num'
  int fraction_get_numerator(Fraction *this);

  // instance method to multiply a fraction with another one
  void fraction_multiply(Fraction this, Fraction that);

The struct along with the functions constitute the class ``Fraction``. Two basic principles establish
the class character, purely by convention:

* the struct and its associated functions use a common name prefix (``fraction``)
* all functions operating on an instance of the struct (i.e. "methods") take that instance as the first argument.

It is straightforward to use this type from Scala Native ``@extern`` bindings:

.. code-block:: scala

  @extern
  object Fraction {
    def fraction_new(num: Int, denom: Int): Ptr[Byte] = extern
    def fraction_get_numerator(f: Ptr[Byte]): Int
    // ...
  }

  val f = Fraction.fraction_new(2,3)
  val g = Fraction.fraction_new(6,5)
  Fraction.fraction_multiply(f,g)

However, this approach does not only result in tedious code, but it is also not type-safe,
since instances of any other C class would be represented by a `Ptr[Byte]` as well.

It would be more idiomatic and safe, if we could use this C type like a normal Scala class, and also
define it like one:

.. code-block:: scala

  import scalanative._
  import unsafe._
  import cobj._

  @CObj
  class Fraction {
    def getNumerator(): Int = extern
    def multiply(that: Fraction): Unit = extern
  }
  object Fraction {
    def apply(num: Int, denom: Int): Fraction = extern
  }

  val f = Fraction(2,3)
  val g = Fraction(6,5)
  f.multiply(g)

Indeed, the previous example shows how to define the C type as a Scala class with swog.
The following properties distinguish `Fraction` from a "normal" Scala class:

``@CObj`` annotation:
  This is actually a `macro annotation <https://docs.scala-lang.org/overviews/macros/annotations.html>`_
  that generates the required wrapper code on the fly during compilation. Don't forget to add this
  annotation to all classes representing an external C type.

``extern`` methods:
  Similar to functions in ``@external`` objects, the body of external instance methods is replaced with ``extern``.

Instantiation:
  From the user's point of view, the most notable difference arises on instantiation:
  ``@CObj`` classes are (usually) not instantiated using ``new`` but by calling a function
  on its companion object.

Function names:
  As it is customary in Scala, the snake_case names of the C functions are replaced with camelCase
  notation. Furthermore, the leading `fraction_` prefix of the C function names has been dropped,
  since it is superfluous in Scala.
  However, the mapping between Scala names and C names is configurable, and may be overwritten
  for every function using the standard ``@name`` annotation.

.. important::

  As the next sections will explain, swog is quite flexible in how to map a C type with a corresponding set
  of functions into a Scala class. However there is *one hard rule* to which the C API must adhere to:

  **Every C function that operates on an instance of the type, must take a pointer to that type as its first argument.**

Basic Principles
================

Naming Convention
-----------------
TODO

Instantiation
-------------
TODO

De-allocation
-------------
TODO

Under the Hood & Debugging
--------------------------
TODO

Enums
=====
TODO

Inheritance
===========
TODO

Generics & Wrapper Factories
============================
TODO

Out Parameters & Return-by-Value
================================
TODO


Implicit Constructor Params
===========================
TODO