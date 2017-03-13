name := "metric-digest"

organization := "com.manyangled"

version := "0.1.0"

scalaVersion := "2.11.8"

crossScalaVersions := Seq("2.11.8", "2.12.1")

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-clients" % "0.10.2.0",
  "org.apache.kafka" % "kafka_2.11" % "0.10.2.0",
  "org.scalatest" %% "scalatest" % "3.0.1" % Test
)

licenses += ("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0"))

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

scalacOptions in (Compile, doc) ++= Seq("-doc-root-content", baseDirectory.value+"/root-doc.txt")
