Embedding and Inter-Op with Lua 5.3
***********************************

Introduction
============
The ``swog-lua`` bridge supports embedding a Lua 5.3 interpreter into a SN application,
reading and writing Lua objects, and of course exposing SN objects to the Lua interpreter.

Example
-------

1. Create a Lua instance, load Lua standard libraries and SN extensions:

.. code-block:: scala

  import lua._

  val lua = Lua()
  lua.init()

2. Execute a script string that defines a global Lua table ``config``:

.. code-block:: scala

  lua.execString(
    """config = {
      |foo = 42,
      |  bar = {
      |    string = "Hello world!"
      |  }
      |}""".stripMargin)

3. Access the values stored in ``config`` from SN:

.. code-block:: scala

  lua.getGlobalValue("config") match {
    case Some(obj: LuaTable) =>
      println( obj.toMap() )  // convert the Lua table into a Scala map
  }

4. Define and register a Scala class that is accessible from Lua:

.. code-block:: scala

  package bar
  import scalanative.scriptbridge._

  @ScriptObj
  class Foo(var num: Int) {
    def incr(): Int = num += 1
  }

  object Foo extends LuaModule {
  }

  lua.registerModule(Foo)

5. Use SN class from Lua:

.. code-block:: scala

  lua.execString(
      """-- load Scala class
        |Foo = scala.load("Foo")
        |-- create new instance
        |foo = Foo.new(42)
        |-- print current value of num
        |print(foo:num())
        |-- call Scala method incr()
        |foo:incr()
        |print(foo:num())
        |-- set num = -1
        |foo:setNum(-1)
        |print(foo:num())
        |""".stripMargin)

6. Close Lua instance:

.. code-block:: scala

  lua.free()


Calling Lua from Scala
======================
TODO

Calling Scala from Lua
======================

Three steps are required to expose a Scala ``class`` or ``object`` to a Lua interpreter:

1. Create a class / object annotated with `@ScriptObj`,

2. Register it to the Lua instance from which you want to call it,

3. Load the Scala class into the Lua interpreter.

Basic Rules for Objects exposed to Lua
--------------------------------------

Here is the basic template for SN classes exposed to Lua:

.. code-block:: scala

  import scalanative.scriptbridge._
  import lua._

  @ScriptObj
  // the primary constructor will be exposed to Lua as function 'Foo.new(i)'
  class Foo(i: Int) {

    // exposed to Lua as:
    //   foo:incr()     (get)
    //   foo:setIncr(i) (set)
    var incr: Int = 1

    // exposed to Lua as foo:add(i)
    def add(a: Int): Unit = // ...

    // not accessible from Lua
    private def bar(): Unit = // ...

  }

  // it's not required to extend LuaModule,
  // but it will prevent IDEs from complaining when we call lua.registerModule(Foo)
  object Foo extends LuaModule {
    // exposed to Lua as Foo.default()
    val default: Foo = new Foo(42)

    // exposed to Lua as Foo.bar(i)
    def bar(i: Int): Int = // ...
  }


Keep the following rules in mind when you design your Lua bridge object:

* You can export only a Scala ``object`` without a companion class, only a Scala class, or both.
  However, if you define both, only one can be annotated with ``@ScriptObj``.

* If you define a script bridge class ``Foo``, its primary constructor is automatically exposed
  to Lua as ``Foo.new()``. Secondary constructors are *not exposed* to Lua.

* By default, every public method, ``val`` and ``var`` is exposed to Lua.
  However, if a member is annotated with ``@nolua`` it will not be exposed to Lua.

* By default, the Lua name of a member is identical to the Scala name, with the prefix `set` for Scala setters.
  However, you can override this name by annotating the member with ``@luaname()```

* All members exposed to Lua need an explicit type, i.e. the following will result in an error:

  .. code-block:: scala

    @ScriptObj
    class Foo {
      var i = 42  // error: exposed member needs excplicit type annotation 'Int'
    }

* Overloading of exposed methods is **not supported**, i.e. the following will result in a
  compile-time error:

  .. code-block:: scala

    @ScriptObj
    class Foo {
      def bar(): Unit = // ...
      def bar(i: Int): Int = // ...
    }

Type Mapping
------------
TODO

Advanced Topics
---------------
TODO