lazy val twitter = project
  .in(file("."))
  .settings(
    name := "twitter-fav-search-twitter-module",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "3.1.0",
    libraryDependencies += "org.twitter4j" % "twitter4j-core" % "4.0.7",
    libraryDependencies += "org.twitter4j" % "twitter4j-async" % "4.0.7"
  )
