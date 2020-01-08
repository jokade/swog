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

It is straightforward to use this type with Scala Native ``@extern`` bindings:

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
since instances of any other C class would be represented by a ``Ptr[Byte]`` as well.

It would be more idiomatic and safe, if we could use this C type like a normal Scala class, and also
define it like one:

.. code-block:: scala

  import scalanative._
  import unsafe._
  import cobj._

  @CObj
  class Fraction extends CObject {
    def getNumerator(): Int = extern
    def multiply(that: Fraction): Unit = extern
  }
  object Fraction {
    @name("fraction_new")
    def apply(num: Int, denom: Int): Fraction = extern
  }

  val f = Fraction(2,3)
  val g = Fraction(6,5)
  f.multiply(g)

Indeed, the previous example shows how to define the C type as a Scala class with swog.
The following properties distinguish ``Fraction`` from a "normal" Scala class:

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
  notation. Furthermore, the leading ``fraction_`` prefix of the C function names has been dropped,
  since it is superfluous in Scala.
  However, the mapping between Scala names and C names is configurable, and may be overwritten
  for every function using the standard ``@name`` annotation.

.. important::

  As the next sections will explain, swog is quite flexible in how to map a C type with a corresponding set
  of functions into a Scala class. However there is *one hard rule* to which the C API must adhere to:

  **Every C function that operates on an instance of the type, must take a pointer to that type as its first argument.**


Basic Principles
================

Name Mapping
------------
While there are various established patterns for function names in C, in Scala most of us will prefer camelCase for property and
method names, and PascalCase for types. In order to avoid explicit ``@name()`` annotations as much as possible,
swog provides autmatic mapping of camelCase to snake_case or PascalCase out-of-the box.

For example, this Scala wrapper definition for the `Gtk+ label widget <https://developer.gnome.org/gtk3/stable/GtkLabel.html>`_:

.. code-block:: scala

  @CObj
  class GtkLabel extends GtkWidget {

    def getText(): CString = extern

    def setText(text: CString) = extern

    // ...
  }

maps to calls to the following snake_case C function declarations:

.. code-block:: C

  char* gtk_label_get_text();

  void gtk_label_set_text(char* text);

This is the default name mapping for methods in swog ``@CObj`` wrappers, which
- translates all uppercase letters their lowercase counterpart followed by an underscore,
- and joins prefixes all methods with the (unqualified) class name, also mapped to snake case.

We can also use PascalCase mapping by explicitly passing it as a parameter to the ``@CObj`` annotation:

.. code-block:: Scala

  @CObj(namingConvention = NamingConvention.PascalCase)
  class GtkLabel extends GtkWidget {

    def getText(): CString = extern

    def setText(text: CString) = extern

    // ...
  }

would bind to

.. code-block:: C

  char* GtkLabelGetText();

  void* GtkLabelSetText();

Two other naming conventions are currently supported: ``LowerCase``, which simply replaces all uppercase letters
with lower case, without any further transformation; and ``None`` which gives you full control by doing no transformation
at all.

.. important::

  You can always override the generated name individually for each method using the standard ``@name`` annotation.
  For example, if you prefer your getters without the ``get`` prefix, but keeping the prefix on setters, you could
  achieve it like this:

  .. code-block:: Scala

    @CObj
    class GtkLabel extends GtkWidget {

      @name("gtk_label_get_text")  // <- overwrite generated external name
      def text(): CString = extern

      def setText(text: CString) = extern

      // ...
    }

  However, when using ``@name`` you must provide the full name of the external function (i.e. no prefix is
  generated in this case).

Sometimes the common C prefix of the functions making up a class, is not the prefix we want to use as the name of our
Scala wrapper, or it simply does not conform to one of the supported naming conventions. In this case we can pass the
prefix as argument to ``@CObj``. For example, if we wanted to drop the `Gtk` from all our Scala wrappers, we could
achieve this by setting:

.. code-block:: Scala

  @CObj(prefix = "gtk_label_")
  class Label extends Widget {

    def getText(): CString = extern

    def setText(text: CString) = extern

    // ...
  }

which would again result in the correct C function names ``gtk_label_get_text`` and ``gtk_label_set_text``.
Of course, setting ``prefix = ""`` we can get rid of the common prefix entirely.

.. note::

  The reason why snake_case is applied as default naming convention is that swog was originally written as bindings
  generator for the GLib/Gtk+ ecosystem, which sticks to that convention consistently, and is arguably one of the most extensive
  set of purely C-based, object-oriented libraries.

Wrappers are (Almost) Normal Scala Types / Objects
--------------------------------------------------
Aside from ``extern`` methods, ``@CObj`` wrappers may contain most other members that are supported by "normal"
classes/objects (the exception being nested ``@CObj`` annotations). For example we can extend our ``GtkLabel`` wrapper
with a Scala-style property ``text`` that operates on Scala ``String`` s instead of ``CString`` s:

.. code-block:: Scala

  @CObj(prefix = "gtk_label_")
  class Label extends Widget {

    def getText(): CString = extern

    def setText(text: CString) = extern

    def text: String = fromCString(getText())
    def text_=(s: String)(implicit zone: Zone): Unit = setText(toCString(s))
  }

As we've already seen, ``@CObj`` classes can also extend other classes, but the parent must always be a subtype of
``scalanative.cobj.CObject``. Of course, we can also mixin ``traits`` which may or may not derive from ``CObject``.

However, there is one important restriction for ``@CObj`` wrapper classes: they must have a no-arg (empty)
``primary`` constructor, or a primary constructor with the single argument ``__ptr: Ptr[Byte]``.

Instantiation
-------------
Since C was not explicitly designed with the object-oriented metaphor in mind, there's is also no explicit language-level
support for object allocation with simultaneous initialization. Some C libraries that follow the OO pattern simply rely on
the user allocating and initializing a structure explicitly, but the more sophisticated libraries (and those that
support some form of class hierarchy in particular) -- usually provide one or more functions that serve as
'constructors' for the class.

These constructors are identified as such purely by convention of naming; most will simply append ``_new`` to the class
name, e.g.

.. code-block:: C

  GtkLabel* gtk_label_new(const char* text);

will create a new Gtk label widget with the given text.

Since instantiation in C usually comes down to calling a constructor function, we can't map Scala's ``new`` to calling
that function (at least not when operating with macro expansion, as utilized by swog). Instead, we declare the constructor
as an external binding on the companion object of our wrapper class. For example:

.. code-block:: Scala

  @CObj
  class GtkLabel extends GtkWidget {
    // ...
  }
  object {
    @name("gtk_label_new")
    def apply(text: CString): GtkLabel = extern
  }

  val myLabel = GtkLabel(c"Hello world!")

There's nothing special about ``apply()``, it's a normal external binding to the C constructor function that returns the
newly initialized Gtk label object. However, we must provide an explicit ``@name()`` since otherwise a call to
``gtk_label_apply()`` would be generated.

Using ``apply()`` is usually convenient and idiomatic Scala; but you can use any name for the constructor
(as long as you map it correctly with ``@name()``); it isn't even required to be defined in the companion object of the
class, as long as it is defined in a object or class annotated with ``@CObj`` (a vanilla ``@extern`` object will *not* do).

De-allocation
-------------
TODO

Type Mapping
------------
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


Tips & Tricks
=============

* Always use implicit wrappers for bindings that return a wrapped C object, i.e.

.. code-block:: scala

   def getFoo()(implicit w: CObjectWrapper[Foo]): Foo = extern

instead of

.. code-block:: scala

   def getFoo(): Foo = extern

This way, it is possible to use a custom wrapper when the method is called (e.g. a singleton wrapper to improve
performance with operations on large collections or nested hierarchies).