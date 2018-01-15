import play.sbt.PlayImport.PlayKeys.playRunHooks

name := """bitmail"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
    .enablePlugins(PlayScala)
    .settings(
        watchSources ++= (baseDirectory.value / "public/ui" ** "*").get
    )

scalaVersion := "2.11.11"

libraryDependencies += jdbc
libraryDependencies += cache
libraryDependencies += ws
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test
libraryDependencies += specs2 % Test
libraryDependencies += "org.reactivemongo" %% "play2-reactivemongo" % "0.12.5-play25"
libraryDependencies += "com.codeborne" % "phantomjsdriver" % "1.2.1"
libraryDependencies += "fr.acinq" % "bitcoin-lib_2.11" % "0.9.13"
libraryDependencies += "com.typesafe.play" %% "play-mailer" % "6.0.1"
libraryDependencies += "com.typesafe.play" %% "play-mailer-guice" % "6.0.1"

playRunHooks += baseDirectory.map(Webpack.apply).value

routesGenerator := InjectedRoutesGenerator

pipelineStages := Seq(digest, gzip)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
