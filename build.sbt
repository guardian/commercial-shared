name := "commercial-shared"

organization := "com.gu"

homepage := Some(url("https://github.com/guardian/marley"))
licenses := Seq("Apache V2" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

scalaVersion := "2.13.1"
crossScalaVersions := Seq(scalaVersion.value, "2.12.10", "2.11.12")

libraryDependencies ++= Seq(
  "com.gu"        %% "content-api-models-scala" % "15.4" % Provided,
  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
  "com.typesafe.play" %% "play-json" % "2.7.4" % Test
)

import ReleaseTransformations._

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("publishSigned"),
  releaseStepCommand("sonatypeBundleRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
