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

Basic Rules for Exposing Objects to Lua
---------------------------------------

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
When a Scala method is called from Lua, the following mapping rules are applied to arguments and return values:

Boolean, Numbers, and Strings
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Boolean, integer, and floating point numbers and strings will be translated into the corresponding Lua/Scala type.

However, keep in mind that Lua numbers will be represented as ``Long`` or ``Double`` in Scala by default. If you want to have
an ``Int`` or ``Float`` you need to cast explicitly. This is particularly important if you access a Lua value from Scala with
``Lua.getValue(): Any``, ``Lua.getGloablValue(): Option[Any]``, or ``LuaTable.get(): Option[Any]``,
since a pattern match on ``Int`` or ``Float`` will fail.


Lua Tables (Objects)
^^^^^^^^^^^^^^^^^^^^
If you want to pass a Lua table to a Scala method, you must define the corresponding Scala argument to be of type
``LuaTable`` or ``immutable.Map``.

However, you should prefer ``LuaTable`` unless you will convert it to a ``Map`` anyway, since this will recursively
convert all nested tables as well.

A Scala return value of type `Map[String,Any]` will ne converted into a Lua table.

*Scala*:

.. code-block:: scala

  @ScriptObj
  object Foo {
    def callWithTable(obj: LuaTable): Unit = {
      // get value of property 'foo'
      println( obj.getOrElse("foo",0) )
    }

    def callWithMap(obj: Map[String,Any]): Map[String,Any] = {
      // access Lua value obj.bar.string
      obj("bar") match {
        case m: Map[_,_] =>
          println( m("string") )
      }
      obj.updated("foo",43)
    }
  }

*Lua*:

.. code-block:: lua

  obj = {
    foo = 42
    bar = {
      string = "hello"
    }
  }

  Foo.callWithTable(obj)
  upd = Foo.callWithMap(obj)
  print( upd.foo ) -- prints 43


Lua nil and Option
^^^^^^^^^^^^^^^^^^
A Scala ``Option`` will be converted to the corresponding Lua value (if it is ``Some()``),
or to Lua ``nil`` if it is ``None``.

The inverse rule is applied for arguments of type ``Option[Any]``.

*Scala*:

.. code-block:: scala

  @ScriptObj
  object Foo {
    def withOption(in: Option[Any]): Option[Long] = in match {
      case Some(l: Long) => Some( l+1 )
      case _ => None
    }
  }

*Lua*:

.. code-block:: lua

  Foo.withOption(41)    -- returns 42
  Foo.withOption(nil)   -- returns nil
  Foo.withOption("foo") -- also returns nil


Advanced Topics
---------------
TODO