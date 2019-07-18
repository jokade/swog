swog - ScalaNative Wrapper Object Generator
===========================================

**[Documentation](http://jokade.surfice.de/swog/)**

sbt Settings
------------

### CObj support
```scala
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies += "de.surfice" %%% "swog-cobj" % "0.1.0-SNAPSHOT"
