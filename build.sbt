import sbtversionpolicy.withsbtrelease.ReleaseVersion

name := "commercial-shared"

organization := "com.gu"

homepage := Some(url("https://github.com/guardian/commercial-shared"))
licenses := Seq(License.Apache2)

scalaVersion := "2.13.12"
scalacOptions := Seq("-release:11")
crossScalaVersions := Seq(scalaVersion.value, "2.12.18")

libraryDependencies ++= Seq(
  "com.gu"        %% "content-api-models-scala" % "19.0.0" % Provided,
  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
  "com.typesafe.play" %% "play-json" % "2.10.4" % Test
)

import ReleaseTransformations._

releaseCrossBuild := true // true if you cross-build the project for multiple Scala versions
releaseVersion := ReleaseVersion.fromAggregatedAssessedCompatibilityWithLatestRelease().value
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  setNextVersion,
  commitNextVersion
)
