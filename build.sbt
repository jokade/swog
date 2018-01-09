organization in ThisBuild := "de.surfice"

version in ThisBuild := "0.0.1-SNAPSHOT"

scalaVersion in ThisBuild := "2.11.12"

val Version = new {
  val smacrotools = "0.0.7-SNAPSHOT"
  val utest       = "0.6.3"
}


lazy val commonSettings = Seq(
  scalacOptions ++= Seq("-deprecation","-unchecked","-feature","-language:implicitConversions","-Xlint"),
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "utest" % Version.utest % "test"
    ),
  testFrameworks += new TestFramework("utest.runner.Framework")
  )


lazy val interop = project.in(file("."))
  .enablePlugins(ScalaNativePlugin)
  .settings(commonSettings:_*)
  .settings(
    name := "scalanative-obj-interop",
    libraryDependencies ++= Seq(
      "de.surfice" %% "smacrotools" % Version.smacrotools
    )
  )

lazy val tests = project
  .enablePlugins(ScalaNativePlugin)
  .dependsOn(interop)
  .settings(commonSettings ++ dontPublish:_*)
  .settings(
    nativeLinkStubs := true,
    nativeLinkingOptions ++= Seq(
      "-lglib-2.0",
      "-lgtk-3.0"
    )
  )

lazy val dontPublish = Seq(
  publish := {},
  publishLocal := {},
  com.typesafe.sbt.pgp.PgpKeys.publishSigned := {},
  com.typesafe.sbt.pgp.PgpKeys.publishLocalSigned := {},
  publishArtifact := false,
  publishTo := Some(Resolver.file("Unused transient repository",file("target/unusedrepo")))
)
