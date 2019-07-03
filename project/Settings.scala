import sbt._
import Keys._

object Settings {
  object scala {
    val version = "2.13.0"
    val commonSettings = Seq(
      scalaVersion := version,
      organization := "io.github.methrat0n",
      scalacOptions := Seq (
        "-encoding", "utf-8",
        "-explaintypes",
        "-deprecation",
        "-unchecked",
        "-feature",
        //"-Ywarn-unused",
        "-Xcheckinit",
        "-Xfatal-warnings",
        "-Xlint",
        "-Xlog-implicits"
      )
    )
  }
}
