import com.typesafe.startscript.StartScriptPlugin

organization := "name.jaredarmstrong.example"

name := "todo"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.9.1"

seq(webSettings :_*)

libraryDependencies ++= Seq(
  "org.scalatra"		%% "scalatra"			% "2.1.0-SNAPSHOT",
  "org.scalatra"		%% "scalatra-scalate"	% "2.1.0-SNAPSHOT",
  "org.scalatra"		%% "scalatra-lift-json"	% "2.1.0-SNAPSHOT",
  "org.scalatra"		%% "scalatra-auth"	% "2.1.0-SNAPSHOT",
  "org.eclipse.jetty" % "jetty-webapp" % "8.1.0.RC5" % "container;test",
  "org.eclipse.jetty" % "jetty-servlets" % "8.1.0.RC5" % "container;test",
  "org.eclipse.jetty" % "jetty-webapp" % "8.1.0.RC5",
  "org.eclipse.jetty" % "jetty-servlets" % "8.1.0.RC5",
  "javax.servlet" % "javax.servlet-api" % "3.0.1" % "provided",
  "ch.qos.logback"		% "logback-classic" % "1.0.0" % "runtime",
  "com.googlecode.flyway" % "flyway-core" % "1.5",
  "play" %% "anorm" % "2.0.1",
  "com.jolbox" % "bonecp" % "0.7.1.RELEASE",
  "com.h2database" % "h2" % "1.3.167",
  "net.liftweb" %% "lift-json" % "2.4",
  "net.liftweb" %% "lift-common" % "2.4"
)

resolvers += "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

seq(StartScriptPlugin.startScriptForClassesSettings: _*)
