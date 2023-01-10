import sbtassembly.MergeStrategy

import java.nio.file.{Files, Path, Paths}
import scala.language.postfixOps
import scala.sys.process.Process

ThisBuild / scalaVersion := "2.13.10"

logBuffered := false
/**
 * This addresses a bug in sbt-assembly that does not create the output directory, by creating the target/result directory.
 * @see https://github.com/sbt/sbt-assembly/issues/486
 */
lazy val output: File = {
  val jarsPath = Paths.get("target/result").toAbsolutePath
  if (Files.notExists(jarsPath)) {
    val created = Files.createDirectories(jarsPath)
    println(s"created: ${created.toFile}")
  }
  jarsPath.resolve("rotatorcontrol.jar").toFile
}


lazy val rotatorcontrol = (project in file("."))
  .settings(
//    assembly / assemblyJarName := "rotatorcontrol.jar",
    assembly / assemblyOutputPath := output,
    assembly / assemblyMergeStrategy := {
      case p if p.startsWith("javafx") =>
        MergeStrategy.discard
      case PathList("META-INF", xs@_*) =>
        MergeStrategy.discard
      case _ =>
        MergeStrategy.first
    }
  )

fork := false

enablePlugins(JvmPlugin, GitPlugin, BuildInfoPlugin)
buildInfoKeys ++= Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion,
  git.gitCurrentTags, git.gitCurrentBranch, git.gitHeadCommit, git.gitHeadCommitDate, git.baseVersion)
buildInfoPackage := "com.wa9nnn.rotator"

buildInfoOptions ++= Seq(
  BuildInfoOption.ToJson,
  BuildInfoOption.BuildTime
)

resolvers += ("Reposilite" at "http://194.113.64.105:8080/releases")
  .withAllowInsecureProtocol(true)

val logbackVersion = "1.2.3"

libraryDependencies ++= Seq(
  "com.wa9nnn" %% "util" % "0.1.9",
  "org.scalafx" %% "scalafx" % "19.0.0-R30" excludeAll (
    ExclusionRule(organization = "org", "openjfx"),
    ),
  "org.scalafx" %% "scalafx-extras" % "0.7.0",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",

  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "ch.qos.logback" % "logback-core" % logbackVersion,

  "org.specs2" %% "specs2-core" % "4.6.0" % "test",
  "org.specs2" %% "specs2-mock" % "4.6.0" % "test",
  "com.github.scopt" %% "scopt" % "4.0.1",

  "org.jfree" % "jfreechart" % "1.5.3",
  "org.jfree" % "jfreechart-fx" % "1.0.1",
  "com.typesafe.play" %% "play-json" % "2.9.3",
  "com.google.inject" % "guice" % "5.1.0",
  "net.codingwell" %% "scala-guice" % "5.1.0",
  "nl.grons" %% "metrics4-scala" % "4.2.9",
  "io.dropwizard.metrics" % "metrics-jmx" % "4.2.13",
  "com.typesafe" % "config" % "1.4.2",
)

publish / skip := true


import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._


val ghRelease = taskKey[Unit]("send stuff to github")

//val buildFatJar = taskKey[Unit]("Build fat jat and publish version string")
//buildFatJar := {
//  val log = streams.value.log
//  try {
//    log.info("=========buildFatJar=========")
//
//    val resultDir = Files.createDirectories(Paths.get("target/result"))
//    Files.createDirectories(resultDir)
//    val pubArtifact: File = (assembly).value
//    log.info(s"pubArtifact: $pubArtifact")
//    val versionFile = resultDir.resolve("version.txt")
//    val sVersion = version.value.replace("-SNAPSHOT", "")
//    try {
//      Files.writeString(versionFile, sVersion)
//      log.info(s"$sVersion  written to $versionFile")
//    } catch {
//      case e:Exception =>
//        log.error(e.getMessage)
//    }
//
//  } catch {
//    case e: Exception =>
//      e.printStackTrace()
//  }
//}

ghRelease := {
  val log = streams.value.log
  try {
    log.info("=========ghRelease=========")

    val pubArtifact: File = (assembly).value
    log.info(s"pubArtifact: $pubArtifact")

    val github: java.nio.file.Path = Paths.get("github.sh")
    log.info(s"github path: $github Executable: ${Files.isExecutable(github)}")

    val abs: File = github.toAbsolutePath.toFile
    log.info(s"github abs: $abs")

    log.info(s"pubArtifact: $pubArtifact")

    val cmd = s"""gh release create --notes-file docs/relnotes.txt  v${version.value} $pubArtifact"""
    log.info((s"cmd: $cmd"))
    Process(cmd) ! log
    log.info(s"\tcmd: $cmd done")
  } catch {
    case e: Exception =>
      e.printStackTrace()
  }
}

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies, // : ReleaseStep
  inquireVersions, // : ReleaseStep
  runClean, // : ReleaseStep
  runTest, // : ReleaseStep
  setReleaseVersion, // : ReleaseStep
  commitReleaseVersion, // : ReleaseStep, performs the initial git checks
  //  tagRelease, // : ReleaseStep
  releaseStepTask(ghRelease),
  //  releaseStepTask(Universal / packageBin),
  //  publishArtifacts,                       // : ReleaseStep, checks whether `publishTo` is properly set up
  //  pushChanges, // : ReleaseStep, also checks that an upstream branch is properly configured
  setNextVersion, // : ReleaseStep
  commitNextVersion, // : ReleaseStep
  pushChanges, // : ReleaseStep, also checks that an upstream branch is properly configured
)

resolvers +=
  "ReposiliteXYZZY" at "http://127.0.0.1:8080/releases"

ThisBuild / assemblyMergeStrategy := {
  case p if p.startsWith("javafx") =>
    MergeStrategy.discard
  case PathList("META-INF", xs@_*) =>
    MergeStrategy.discard
  case _ =>
    MergeStrategy.first
}