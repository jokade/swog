organization in ThisBuild := "de.surfice"

version in ThisBuild := "0.0.7-SNAPSHOT"

scalaVersion in ThisBuild := "2.11.12"

val Version = new {
  val smacrotools = "0.0.8"
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
  .aggregate(common,cobj,objc)
  .settings(commonSettings ++ dontPublish:_*)
  .settings(
    name := "scalanative-obj-interop"
  )

lazy val common = project
  .enablePlugins(ScalaNativePlugin)
  .settings(commonSettings ++ publishingSettings:_*)
  .settings(
    name := "scalanative-interop-common",
    libraryDependencies ++= Seq(
      //"org.scala-native" %%% "posixlib" % "0.3.9-SNAPSHOT"
    )
  )

lazy val cobj = project
  .enablePlugins(ScalaNativePlugin)
  .dependsOn(common)
  .settings(commonSettings ++ publishingSettings:_*)
  .settings(
    name := "scalanative-interop-cobj"
  )

lazy val objc = project
  .enablePlugins(ScalaNativePlugin)
  .dependsOn(common)
  .settings(commonSettings ++ publishingSettings:_*)
  .settings(
    name := "scalanative-interop-objc"
  )

import scalanative.sbtplugin.ScalaNativePluginInternal._

lazy val cobjTests = project
  .enablePlugins(ScalaNativePlugin,NBHAutoPlugin,NBHMakePlugin)
  .dependsOn(cobj)
  .settings(commonSettings ++ dontPublish:_*)
  .settings(
    nativeLinkStubs := true,
    nativeLinkingOptions ++= Seq(
      "-lglib-2.0",
      "-lgobject-2.0",
      "-lgtk-3.0"
    ),
    nbhMakeProjects += NBHMakeProject(baseDirectory.value / "src" / "test" / "c" ,Seq(NBHMakeArtifact("mockups.o")))
  )

lazy val objcTests = project
  .enablePlugins(ScalaNativePlugin,NBHAutoPlugin,NBHMakePlugin)
  .dependsOn(objc)
  .settings(commonSettings ++ dontPublish: _*)
  .settings(
    nativeLinkStubs := true,
    nbhMakeProjects += NBHMakeProject(baseDirectory.value / "src" / "test" / "objc" ,Seq(NBHMakeArtifact("mockups.o"))),
    nbhLinkFrameworks += "Foundation"
  )

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
        <name>MIT License</name>
        <url>http://www.opensource.org/licenses/mit-license.php</url>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:jokade/scalantive-obj-interop</url>
      <connection>scm:git:git@github.com:jokade/scalantive-obj-interop.git</connection>
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

