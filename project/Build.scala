import sbt.Keys._
import sbt._

object UtilBuild extends Build {
  object V {
    val Akka = "2.3.9"
    val Specs2 = "3.0"
  }

  lazy val compilerWarnings = Seq(
    "-Ywarn-dead-code",
    "-Ywarn-infer-any",
    "-Ywarn-numeric-widen",
    "-Ywarn-unused"
  )

  val publishSettings: Seq[Def.Setting[_]] = Seq(
    licenses := Seq("MIT" -> url("http://www.opensource.org/licenses/MIT")),
    publishTo <<= version.apply { v =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra := (
      <url>http://github.com/actorapp/actor-util</url>
      <licenses>
        <license>
        <name>MIT</name>
        <url>http://www.opensource.org/licenses/MIT</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:actorapp/actor-util.git</url>
        <connection>scm:git:git@github.com:actorapp/actor-util.git</connection>
      </scm>
      <developers>
        <developer>
          <id>prettynatty</id>
          <name>Andrey Kuznetsov</name>
          <url>http://fear.loathing.in</url>
        </developer>
      </developers>
    )
  )

  val sharedSettings: Seq[Def.Setting[_]] = Seq(
    organization := "im.actor",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.11.5",
    scalacOptions in Compile  ++= Seq(
      "-encoding",
      "UTF-8",
      "-deprecation",
      "-unchecked",
      "-feature",
      "-language:higherKinds"
    ) ++ compilerWarnings
  ) ++ publishSettings

  lazy val actorUtil = Project(
    id = "actor-util",
    base = file("."),
    settings = Defaults.coreDefaultSettings ++ sharedSettings
  ).aggregate(actorUtilTesting)

  lazy val actorUtilTesting = Project(
    id = "actor-util-testing",
    base = file("util-testing"),
    settings = Defaults.coreDefaultSettings ++ sharedSettings
  ).settings(
    name := "actor-util-testing",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % V.Akka,
      "com.typesafe.akka" %% "akka-testkit" % V.Akka,
      "org.specs2" %% "specs2-core" % V.Specs2
    )
  )
}
