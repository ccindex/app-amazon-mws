name := """wsdm"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "org.apache.poi" % "poi" % "3.8",
  "org.apache.poi" % "poi-ooxml" % "3.9",
  "org.apache.commons" % "commons-compress" % "1.9",
  "org.apache.directory.studio" % "org.apache.commons.io" % "2.4",
  "org.apache.httpcomponents" % "httpclient" % "4.2",
  "xalan" % "xalan" % "2.7.1",
  "org.json" % "json" % "20160212"
)

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.18"
