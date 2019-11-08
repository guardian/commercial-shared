publishMavenStyle := true

sonatypeProfileName := "com.gu"

publishTo in ThisBuild := sonatypePublishToBundle.value

scmInfo in ThisBuild := Some(ScmInfo(
  url("https://github.com/guardian/commercial-shared"),
  "scm:git:git@github.com:guardian/commercial-shared.git"
))

pomExtra in ThisBuild := {
  <developers>
    <developer>
      <id>kelvin-chappell</id>
      <name>Kelvin Chappell</name>
      <url>https://github.com/kelvin-chappell</url>
    </developer>
  </developers>
}
