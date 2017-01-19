name := """commercial-shared"""

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.gu" % "content-api-models-scala" % "11.0",
  "org.scalatest" %% "scalatest" % "3.0.1" % Test,
  "net.liftweb" %% "lift-json" % "2.6.3" % Test
)
