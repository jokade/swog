organization in ThisBuild := "de.surfice"

version in ThisBuild := "0.0.1"

scalaVersion in ThisBuild := "2.11.12"

val Version = new {
  val smacrotools = "0.0.7"
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
  .settings(commonSettings ++ publishingSettings:_*)
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
      "-lgobject-2.0",
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

