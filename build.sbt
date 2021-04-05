import play.core.PlayVersion.akkaVersion

name := """play-java-seed"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.3"

libraryDependencies += guice
libraryDependencies += ws

val AkkaVersion = "2.6.8"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % Test
libraryDependencies += "javadoc" % "javadoc" % "1.3"

libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion
libraryDependencies += "com.google.oauth-client" % "google-oauth-client-jetty" % "1.31.1"
libraryDependencies += "com.google.oauth-client" % "google-oauth-client-java6" % "1.31.1"
libraryDependencies += "com.google.apis" % "google-api-services-youtube" % "v3-rev222-1.25.0"
libraryDependencies += "org.mockito" % "mockito-core" % "3.6.0" % "test"
//libraryDependencies += "com.google.inject" % "guice" % "4.2.2"
// using WS
libraryDependencies += javaWs
libraryDependencies += caffeine

jacocoExcludes in Test := Seq(
  "controllers.Reverse*",
  "controllers.javascript.*",
  "jooq.*",
  "router.Routes*",
  "*.routes*"
)