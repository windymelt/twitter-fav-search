val scala3Version = "3.1.0"
val AkkaVersion = "2.6.18"

lazy val root = project
  .in(file("."))
  .settings(
    name := "twitter-fav-search",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
    libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.30.0",
    libraryDependencies += "org.typelevel" %% "cats-core" % "2.7.0",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
      "com.typesafe.akka" %% "akka-stream" % "2.6.18",
      "com.typesafe.akka" %% "akka-stream-testkit" % "2.6.18" % Test
    ),
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.10",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "test",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.9",
    Test / javaOptions += s"-Dconfig.file=${sourceDirectory.value}/test/resources/application.conf",
    Test / fork := true // to apply config above
  )
