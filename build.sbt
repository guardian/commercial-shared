name := "commercial-shared"

organization := "com.gu"

bintrayOrganization := Some("guardian")
bintrayRepository := "frontend"

licenses += ("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0"))

scalaVersion := "2.12.4"
crossScalaVersions := Seq("2.11.11", scalaVersion.value)

libraryDependencies ++= Seq(
  "com.gu"        %% "content-api-models-scala" % "15.4" % Provided,
  "org.scalatest" %% "scalatest" % "3.0.3" % Test,
  "com.typesafe.play" %% "play-json" % "2.7.4" % Test
)
