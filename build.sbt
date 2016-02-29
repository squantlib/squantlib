import sbt._
import sbt.Keys._

val `app.organization` = "org.jquantlib.foss"
val `app.name` = "qls"

val `java.version`  = "1.7"
val `scala.version` = "2.11.7"

val `otest.version` = "0.2.4"

// compilation options -------------------------------------------------------------------------------------------------

val javacOpts : Seq[String] =
  Seq(
    "-encoding", "UTF-8",
    "-source", `java.version`
  )
val scalacOpts : Seq[String] =
  Seq(
    "-unchecked", "-deprecation", "-feature"
  )
val javacCheckOpts: Seq[String] =
  Seq(
    "-Xlint"
  )
val scalacCheckOpts: Seq[String] =
  Seq(
    "-Xlint",
    "-Yinline-warnings", "-Ywarn-adapted-args",
    "-Ywarn-dead-code", "-Ywarn-inaccessible", "-Ywarn-nullary-override",
    "-Ywarn-nullary-unit", "-Xlog-free-terms"
  )

// settings ------------------------------------------------------------------------------------------------------------

lazy val librarySettings : Seq[Setting[_]] =
  Seq(
    scalaVersion       := `scala.version`,
    fork               :=  true,

    compileOrder in Compile := CompileOrder.JavaThenScala,
    compileOrder in Test    := CompileOrder.JavaThenScala,

    javacOptions  ++= javacOpts,
    scalacOptions ++= scalacOpts
  )
lazy val paranoidOptions : Seq[Setting[_]] =
  Seq(
    javacOptions  in (Compile, compile) ++= javacCheckOpts,
    javacOptions  in (Test, compile)    ++= javacCheckOpts,
    scalacOptions ++= scalacCheckOpts
  )
lazy val optimizationOptions : Seq[Setting[_]] =
  Seq(
    scalacOptions ++= Seq("-optimise")
  )
lazy val documentationOptions : Seq[Setting[_]] =
  Seq(
    javacOptions  in (Compile,doc) ++= Seq("-Xdoclint", "-notimestamp", "-linksource"),
    scalacOptions in (Compile,doc) ++= Seq("-groups", "-implicits")
  )


// test frameworks -----------------------------------------------------------------------------------------------------

// lazy val utestFramework : Seq[Setting[_]] =
//   Seq(
//     libraryDependencies += "com.lihaoyi" %%% "utest" % `utest.version` % "test",
//     testFrameworks += new TestFramework("utest.runner.Framework")
//   )
// lazy val minitestFramework : Seq[Setting[_]] =
//   Seq(
//     libraryDependencies += "org.monifu" %%% "minitest" % `minitest.version` % "test",
//     testFrameworks += new TestFramework("minitest.runner.Framework")
//   )
// lazy val littleSpecFramework : Seq[Setting[_]] =
//   Seq(
//     libraryDependencies += "org.qirx" %% "little-spec" % `little-spec.version` % "test",
//     testFrameworks += new TestFramework("org.qirx.littlespec.sbt.TestFramework")
//   )
lazy val otestFramework : Seq[Setting[_]] =
  Seq(
    libraryDependencies += "biz.cgta" %% "otest-jvm" % `otest.version` % "test",
    testFrameworks += new TestFramework("cgta.otest.runner.OtestSbtFramework")
  )
//TODO: val doctestFramework : Seq[Setting[_]] =
//TODO:   Seq(
//TODO:     doctestTestFramework := DoctestTestFramework.OTest
//TODO:   )


// publish settings ----------------------------------------------------------------------------------------------------

lazy val disablePublishing: Seq[Setting[_]] =
  Seq(
    publishArtifact := false,
    publish := (),
    publishLocal := ()
  )
lazy val publishSettings: Seq[Setting[_]] =
  Seq(
    publishTo := {
      val nexus = "https://bitbucket.org/xkbm/maven-repository"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    pomIncludeRepository := { _ => false },
    pomExtra := {
      <url>http://bitbucket.com/xkbm/alpha2</url>
        <licenses>
          <license>
            <name>Closed Source</name>
          </license>
        </licenses>
        <scm>
          <connection>scm:hg:bitbucket.com/xkbm/alpha2.hg</connection>
          <developerConnection>scm:hg:frgomes@bitbucket.com:xkbm/alpha2.hg</developerConnection>
          <url>http://bitbucket.com/xkbm/alpha2</url>
        </scm>
        <developers>
          <developer>
            <id>frgomes</id>
            <name>Richard Gomes</name>
            <url>http://rgomes-info.blogspot.com</url>
          </developer>
        </developers>
    }
  )


// dependencies --------------------------------------------------------------------------------------------------------

lazy val dependencies: Seq[Setting[_]] =
  Seq(
    libraryDependencies ++= Seq(
      // "com.mchange" % "c3p0" % "0.9.2.1" % "provided",
      // "org.squeryl" %% "squeryl" % "0.9.6-RC1",
      // "com.google.gdata" % "core" % "1.47.1" % "provided",
      // "org.codehaus.jackson" % "jackson-core-asl" % "1.9.10" % "provided",
      // "org.codehaus.jackson" % "jackson-mapper-asl" % "1.9.10" % "provided",
      // "com.mchange" % "mchange-commons-java" % "0.2.3.3" % "provided",
      // "org.jfree" % "jcommon" % "1.0.17" % "provided",
      // "org.jfree" % "jfreechart" % "1.0.14" % "provided",
      // "mysql" % "mysql-connector-java" % "5.1.10" % "provided",

      // com.typesafe"       %% "scalalogging-slf4j"  % "1.1.0" % "optional",
      // org.slf4j"          %  "slf4j-api"           % "1.7.5" % "optional",
      // ch.qos.logback"     %  "logback-classic"     % "1.0.7" % "optional",
      "org.jquantlib.qlj"  %  "core"                % "0.3.0-SNAPSHOT" % "optional",
      "org.apache.commons" %  "commons-lang3"       % "3.1"            % "optional",
      "org.apache.commons" %  "commons-math3"       % "3.0"            % "optional" ))

//----------------------------------------------------------------------------------------------------------------------

def makeRoot(p: sbt.Project): sbt.Project =
  p.settings(
    librarySettings ++ disablePublishing ++
      Seq(
        organization := `app.organization`,
        name := `app.name`): _*)

def makeModule(p: sbt.Project): sbt.Project =
  p.settings(
    librarySettings ++ publishSettings ++ paranoidOptions ++ //TODO: otestFramework ++
      dependencies ++
      Seq(
        organization := `app.organization`,
        name := `app.name` + "-" + name.value): _*)

// projects ------------------------------------------------------------------------------------------------------------

//import EnrichProject._

lazy val root =
  makeRoot(project.in(file(".")))
    .aggregate(core /*,wip,database,chart,util */ )

lazy val core =
  makeModule(project.in(file("core")))
    .settings(dependencies: _*)

lazy val wip =
  makeModule(project.in(file("wip")))
    .settings(dependencies: _*)

lazy val database =
  makeModule(project.in(file("database")))
    .settings(dependencies: _*)

lazy val chart =
  makeModule(project.in(file("chart")))
    .settings(dependencies: _*)

lazy val util =
  makeModule(project.in(file("util")))
    .settings(dependencies: _*)

//----------------------------------------------------------------------------------------------------------------------
