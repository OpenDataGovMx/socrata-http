import sbt._
import Keys._

import com.socrata.socratasbt.SocrataSbt._
import SocrataSbtKeys._
import SocrataUtil._

import com.socrata.socratasbt.CheckClasspath

object SocrataHttp extends Build {
  lazy val http = Project(
    "socrata-http",
    file("."),
    settings = Defaults.defaultSettings ++ socrataSettings() ++ localSettings
  )

  lazy val localSettings = Seq[Setting[_]](
    scalaVersion := "2.10.0",
    crossScalaVersions := Seq("2.8.1", "2.9.2", "2.10.0"),
    compile in Compile <<= (compile in Compile) dependsOn (CheckClasspath.Keys.failIfConflicts in Compile),
    compile in Test <<= (compile in Test) dependsOn (CheckClasspath.Keys.failIfConflicts in Test),
    testOptions in Test ++= Seq(
      Tests.Argument("-oFD")
    ),
    libraryDependencies <++= (scalaVersion, slf4jVersion) { (scalaVersion, slf4jVersion) =>
      Seq(
        "commons-lang" % "commons-lang" % versions.commonsLang,
        "org.eclipse.jetty" % "jetty-jmx" % versions.jetty,
        "org.eclipse.jetty" % "jetty-server" % versions.jetty,
        "org.eclipse.jetty" % "jetty-servlet" % versions.jetty,
        "com.socrata" %% "socrata-utils" % versions.socrataUtils,
        "org.slf4j" % "slf4j-simple" % slf4jVersion % "test"
      ) ++ (scalaVersion match {
        case Is210() => Seq("org.scala-lang" % "scala-reflect" % scalaVersion)
        case _ => Nil
      })
    }
  )

  object versions {
    val commonsLang = "2.4"
    val jetty = "7.5.1.v20110908"
    val socrataUtils = "0.6.0"
  }
}
