Getting Started
***************

.. warning::

  This project is still *experimental* - concepts and the API may change at any time!
  It is expected to reach a more mature status (beta) after SN-0.4.0 has been finalized.


sbt Settings
============

CObj
----
If you want to generate object-oriented bindings for C libraries, add the following to your project settings:

.. code-block:: scala

  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

  resolvers += Resolver.sonatypeRepo("snapshots")

  libraryDependencies += "de.surfice" %%% "swog-cobj" % "0.1.0-SNAPSHOT"

C++
---
**TODO**

Objective-C
-----------
**TODO**


Prerequisites
=============
CObj
----
The master branch of this project (0.1.0-SNAPSHOT) requires SN-0.4.0+.


C++
---
C++ support currently requires an SN-0.4.0-SNAPSHOT `with this PR <https://github.com/scala-native/scala-native/pull/1632>`_.



