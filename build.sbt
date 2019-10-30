name := "commercial-shared"

organization := "com.gu"

bintrayOrganization := Some("guardian")
bintrayRepository := "frontend"

licenses += ("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0"))

scalaVersion := "2.13.1"
crossScalaVersions := Seq(scalaVersion.value, "2.12.10", "2.11.12")

libraryDependencies ++= Seq(
  "com.gu"        %% "content-api-models-scala" % "15.4" % Provided,
  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
  "com.typesafe.play" %% "play-json" % "2.7.4" % Test
)
