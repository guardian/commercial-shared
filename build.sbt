import sbtversionpolicy.withsbtrelease.ReleaseVersion

name := "commercial-shared"

organization := "com.gu"

homepage := Some(url("https://github.com/guardian/commercial-shared"))
licenses := Seq(License.Apache2)

scalaVersion := "2.13.16"
scalacOptions := Seq("-release:11")
crossScalaVersions := Seq(scalaVersion.value, "2.12.20")

libraryDependencies ++= Seq(
  "com.gu"        %% "content-api-models-scala" % "19.0.1" % Provided,
  "org.scalatest" %% "scalatest" % "3.2.19" % Test,
  "org.playframework" %% "play-json" % "3.0.5" % Test
)

dependencyOverrides += "com.fasterxml.jackson.core" %% "jackson-core" % "2.15.0"

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
