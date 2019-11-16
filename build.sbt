organization in ThisBuild := "de.surfice"

version in ThisBuild := "0.1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.11.12"

val Version = new {
  val jna         = "5.5.0"
  val smacrotools = "0.0.9-SNAPSHOT"
  val utest       = "0.6.8-SNAPSHOT"
}


lazy val commonSettings = Seq(
  scalacOptions ++= Seq("-deprecation","-unchecked","-feature","-language:implicitConversions","-Xlint"),
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
  libraryDependencies ++= Seq(
    "de.surfice" %% "smacrotools" % Version.smacrotools,
    "com.lihaoyi" %%% "utest" % Version.utest % "test"
    ),
   testFrameworks += new TestFramework("utest.runner.Framework")
  )


lazy val root  = project.in(file("."))
  .aggregate(
    platformJVM, platformNative,
    commonJVM, commonNative,
    cobjJVM, cobjNative )
  .settings(commonSettings ++ dontPublish:_*)
  .settings(
    name := "swog"
  )

lazy val platform = crossProject(JVMPlatform,NativePlatform).crossType(CrossType.Full)
  .settings(commonSettings ++ publishingSettings: _*)
  .settings(
    name := "swog-platform"
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "net.java.dev.jna" % "jna" % Version.jna
    ),
    test in Test := {
      val log = streams.value.log
      // compile native test mock-up
      Process("make" :: Nil, baseDirectory.value / "src/test/resources") ! log
      (test in Test).value
    }
  )
lazy val platformJVM = platform.jvm
lazy val platformNative = platform.native

lazy val common = crossProject(JVMPlatform,NativePlatform).crossType(CrossType.Full)
  .dependsOn(platform)
  .settings(commonSettings ++ publishingSettings:_*)
  .settings(
    name := "swog-common",
    libraryDependencies ++= Seq(
      //"org.scala-native" %%% "posixlib" % "0.3.9-SNAPSHOT"
    )
  )
lazy val commonJVM = common.jvm
lazy val commonNative = common.native

lazy val cobj = crossProject(JVMPlatform,NativePlatform)
  .dependsOn(common)
  .settings(commonSettings ++ publishingSettings:_*)
  .settings(
    name := "swog-cobj"
  )
lazy val cobjJVM = cobj.jvm
lazy val cobjNative = cobj.native

/*
lazy val objc = project
  .enablePlugins(ScalaNativePlugin)
  .dependsOn(common)
  .settings(commonSettings ++ publishingSettings:_*)
  .settings(
    name := "swog-objc"
  )

lazy val cxx = project
  .enablePlugins(ScalaNativePlugin)
  .dependsOn(cobj)
  .settings(commonSettings ++ publishingSettings: _*)
  .settings(
    name := "swog-cxx"
  )

lazy val cxxlib = project
  .enablePlugins(ScalaNativePlugin)
  .dependsOn(cxx)
  .settings(commonSettings ++ publishingSettings: _*)
  .settings(
    name := "swog-cxxlib"
//    nativeLinkStubs := true,
//    nbhCxxCXXFlags += "-std=c++11"
  )

lazy val scriptbridge = project
  .enablePlugins(ScalaNativePlugin)
  .settings(commonSettings ++ publishingSettings: _*)
  .settings(
    name := "swog-scriptbridge"
  )

lazy val lua = project
  .enablePlugins(ScalaNativePlugin)
  .dependsOn(scriptbridge,cobj)
  .settings(commonSettings ++ publishingSettings: _*)
  .settings(
    name := "swog-lua"
  )

import scalanative.sbtplugin.ScalaNativePluginInternal._
*/
lazy val cobjTests = crossProject(JVMPlatform,NativePlatform)
  //.enablePlugins(ScalaNativePlugin,NBHAutoPlugin,NBHCxxPlugin)
  .dependsOn(cobj)
  .settings(commonSettings ++ dontPublish:_*)
  .settings(
    fork := false
  )
  .nativeSettings(
    nativeLinkStubs := true,
    nativeLinkingOptions ++= Seq(
    )
  )
lazy val cobjTestsJVM = cobjTests.jvm
lazy val cobjTestsNative = cobjTests.native

/*
lazy val objcTests = project
  .enablePlugins(ScalaNativePlugin,NBHAutoPlugin,NBHCxxPlugin)
  .dependsOn(objc)
  .settings(commonSettings ++ dontPublish: _*)
  .settings(
    nativeLinkStubs := true,
    nbhMakeProjects += NBHMakeProject(baseDirectory.value / "src" / "test" / "objc" ,Seq(NBHMakeArtifact("mockups.o"))),
    nbhLinkFrameworks += "Foundation"
  )

lazy val cxxTests = project
  .enablePlugins(ScalaNativePlugin,NBHCxxPlugin)
  .dependsOn(cxx,cxxlib)
  .settings(commonSettings ++ dontPublish: _*)
  .settings(
    nativeLinkStubs := true,
    nbhCxxCXXFlags += "-std=c++11"
  )

lazy val luaTests = project
  .enablePlugins(ScalaNativePlugin,NBHAutoPlugin)
  .dependsOn(lua)
  .settings(commonSettings ++ dontPublish: _*)
  .settings(
    nativeLinkStubs := true,
    //scalacOptions += "-Xmacro-settings:smacrotools.extensions=lua.scriptbridge.LuaScriptBridge",
    nbhPkgConfigModules += "lua-5.3"
  )
*/
lazy val dontPublish = Seq(
  publish := {},
  publishLocal := {},
  com.typesafe.sbt.pgp.PgpKeys.publishSigned := {},
  com.typesafe.sbt.pgp.PgpKeys.publishLocalSigned := {},
  publishArtifact := false,
  publishTo := Some(Resolver.file("Unused transient repository",file("target/unusedrepo")))
)

lazy val publishingSettings = Seq(
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  pomExtra := (
    <url>https://github.com/jokade/scalantive-obj-interop</url>
    <licenses>
      <license>
        <name>Apache 2.0 License</name>
        <url>https://opensource.org/licenses/Apache-2.0</url>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:jokade/swog</url>
      <connection>scm:git:git@github.com:jokade/swog.git</connection>
    </scm>
    <developers>
      <developer>
        <id>jokade</id>
        <name>Johannes Kastner</name>
        <email>jokade@karchedon.de</email>
      </developer>
    </developers>
  )
)

