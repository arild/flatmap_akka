name := "FlatMap Akka"

scalaVersion := "2.9.1"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq("com.typesafe.akka" % "akka-actor" % "2.0.1", "com.typesafe.akka" % "akka-remote" % "2.0.1", "com.typesafe.akka" % "akka-testkit" % "2.0.1", "org.specs2" %% "specs2" % "1.9" % "test", "junit" % "junit" % "4.5" % "test")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.0.0")
