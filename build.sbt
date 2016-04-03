name := "QLS" // project name

version := "1.0" // project version

scalaVersion := "2.11.8" // the current scala version of the project

scalacOptions ++= Seq("-unchecked", "-feature", "-deprecation")

libraryDependencies ++= Seq(
  "org.scalatest"      %% "scalatest"           % "3.0.0-M15",
  "org.jquantlib.java" %  "core"                % "0.3.0-SNAPSHOT",
  "org.apache.commons" %  "commons-lang3"       % "3.1",
  "org.apache.commons" %  "commons-math3"       % "3.0"
)
