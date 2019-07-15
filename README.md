swog - ScalaNative Wrapper Object Generator
===========================================

[Documentation](http://jokade.surfice.de/swog/)



General Notes
===============
The extensions provided by this project use `macro annotations <http://docs.scala-lang.org/overviews/macros/annotations.html>`_ to transform idiomatic Scala classes into the corresponding representation required for interop with the external object system.
You can inspect the generated code by adding the following ``@debug`` annotation to the annottee:

.. code:: Scala

  import scalanative.native._
  import de.surfice.smacrotools.debug

  @CObj
  @debug
  class Foo {
  }

This annotation will print out the generated code to the console during compilation of ``Foo``.

**Important**: due to a current limitation, you must ``import de.surfice.smacrotools.debug`` and then use the unqualified ``@debug`` annotation. Using the qualified annotation doesn't work.

CObj: interop for "informal" C objects
======================================
If we define an object as a data structure that comes with an associated set of functions, then almost all C libraries use objects. Many of these
adhere to the convention, that functions associated with a specific data structure share a common name prefix. Furthermore, functions operating on an instance of that data structure, take a pointer to that instance as the first argument.

One library that adheres to this convention is `Gtk+ <http://www.gtk.org>`_. Here is an extract from the API for GtkButton:

.. code:: C

  /* constructor functions */
  GtkButton* gtk_button_new(void);

  GtkButton* gtk_button_new_with_label(const gchar *label);

  // ...

  /* methods */



Links
=====
* `Scala Native Interop for External Object Systems <https://github.com/jokade/scala-native/blob/topic/external-objects-design/docs/design/external_objects_interop.rst#syntactic-sugar>`_
* `old blog post about the concepts of CObj interop with Gtk+ <http://jokade.surfice.de/scala%20native/2018/01/10/idiomatic-gtk-bindings-for-scalanative/>`_
