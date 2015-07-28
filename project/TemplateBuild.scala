import com.mle.sbtutils.SbtProjects
import sbt._
import sbt.Keys._

/**
 * A scala build file template.
 */
object TemplateBuild extends Build {

  lazy val template = SbtProjects.testableProject("filewatch").settings(projectSettings: _*)

  lazy val projectSettings = Seq(
    version := "0.0.1",
    scalaVersion := "2.11.7",
    fork in Test := true,
    libraryDependencies += "com.github.malliina" %% "util-base" % "0.7.0"
  )
}
