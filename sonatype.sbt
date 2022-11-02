publishMavenStyle := true

sonatypeProfileName := "com.gu"

ThisBuild / publishTo := sonatypePublishToBundle.value

ThisBuild / scmInfo := Some(ScmInfo(
  url("https://github.com/guardian/commercial-shared"),
  "scm:git:git@github.com:guardian/commercial-shared.git"
))

ThisBuild / pomExtra := {
  <developers>
    <developer>
      <id>kelvin-chappell</id>
      <name>Kelvin Chappell</name>
      <url>https://github.com/kelvin-chappell</url>
    </developer>
  </developers>
}
