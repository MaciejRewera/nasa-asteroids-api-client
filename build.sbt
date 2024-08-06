ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.14"


val Http4sVersion = "0.23.15"
val circeVersion = "0.14.9"
val tapirVersion = "1.4.0"
val pureConfigVersion = "0.17.7"
val catsEffectTestingVersion = "1.5.0"

libraryDependencies ++= Seq(

  //Cats
  "org.typelevel" %% "cats-core" % "2.12.0",
  "org.typelevel" %% "cats-effect" % "3.5.1",

  // Http
  "org.http4s" %% "http4s-dsl" % Http4sVersion,
  "org.http4s" %% "http4s-ember-server" % Http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
  "org.http4s" %% "http4s-circe" % Http4sVersion,

  // JSON
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-literal" % circeVersion,

  // Config
  "com.github.pureconfig" %% "pureconfig" % pureConfigVersion,
  "com.github.pureconfig" %% "pureconfig-http4s" % pureConfigVersion,

  // Logger
  "ch.qos.logback" % "logback-classic" % "1.5.6",

  // Test
  "org.scalatest" %% "scalatest" % "3.2.19" % Test,
  "org.mockito" %% "mockito-scala-scalatest" % "1.17.37" % Test,
  "org.typelevel" %% "cats-effect-testing-scalatest" % catsEffectTestingVersion % Test
)

lazy val root = (project in file("."))
  .settings(
    name := "nasa-asteroids-api-client",
    scalafmtOnCompile := true
  )
