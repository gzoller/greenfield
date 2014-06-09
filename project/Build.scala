import sbt._
import sbt.Keys._

import sbtassembly.Plugin.AssemblyKeys._
import sbtbuildinfo.Plugin._
import scala.Some

object Build extends Build {

import Dependencies._

	lazy val basicSettings = Seq(
		organization 				:= "com.bottlerocketapps",
		description 				:= "Web service component for the AWE mobile app",
		startYear 					:= Some(2014),
		// licenses 					:= Seq("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")),
		scalaVersion 				:= "2.11.1",
		parallelExecution in Test 	:= false,
		fork in Test                := true,
		resolvers ++= Dependencies.resolutionRepos,
		scalacOptions				:= Seq("-feature", "-deprecation", "-encoding", "UTF8", "-unchecked"),
		assembleArtifact in packageScala := false,
		testOptions in Test += Tests.Argument("-oDF"),

		version                     := "0.1.0"

		// PUBLISH
		// pomIncludeRepository 		:= { _ => true },
		// publishArtifact in Test 	:= false,
		// publishArtifact in (Compile, packageSrc) := false,
		// publishArtifact in (Compile, packageDoc) := false,
		// publishTo 					:= Some("Artifactory Realm" at "http://artifacts.bottlerocketservices.com/repository/server-development-local/"),
		// credentials 				+= Credentials(Path.userHome / ".sbt" / ".artifactory_credentials")
	)

	lazy val buildSettings = buildInfoSettings ++ Seq(
		sourceGenerators in Compile <+= buildInfo,
		buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion),
		buildInfoPackage := "co.nubilus.roots"
		)

	// configure prompt to show current project
	override lazy val settings = super.settings :+ {
		shellPrompt := { s => Project.extract(s).currentProject.id + " > " }
	}

	// lazy val deps = Project("core-deps", file("."),
	// 	settings = basicSettings ++
	// 				sbtassembly.Plugin.assemblySettings ++
	// 				Seq(
	// 					packageBin in Compile <<= (scalaVersion, version) map { (scalaVersion, version) => file("target/scala-2.10/core-deps_" + scalaVersion.dropRight(2) + "-" + version + ".jar") }, // This magic line causes package to do nothing on the root project
	// 					assemblyOption in assembly ~= { _.copy(includeScala = false) },  // Don't include any Scala libs in the deps jar
	// 					publishArtifact in packageSrc := false,
	// 					publishArtifact in packageDoc := false,
	// 					jarName in assembly <<= (scalaVersion, version) map { (scalaVersion, version) => "core-deps_" + scalaVersion.dropRight(2) + "-" + version + ".jar" },
	// 					libraryDependencies ++=
	// 						// Master list of all used libraries so it gets added to the deps.jar file when you run assembly
	// 						compile(commons_exec, commons_codec, commons_lang, casbah, googleCLHM, joda_time, scalajack, spray_routing, spray_can, spray_client, spray_caching, akka_actor, akka_cluster, akka_slf4j, prettytime, mongo_java, casbah_gridfs, typesafe_config, logback)
	// 					// jarName in assembly <<= (scalaVersion, version) map { (scalaVersion, version) => "core-deps_" + scalaVersion.dropRight(2) + "-" + version + ".jar" }
	// 				)) aggregate(core)

	lazy val roots = Project("roots", file("roots"))
		.settings(basicSettings: _*)
		.settings(buildSettings: _*)
		.settings(libraryDependencies ++=
			// compile(spray_routing, spray_client, spray_can, spray_caching, akka_actor) ++
			compile(akka_actor, spray_routing, spray_can) ++
			test(scalatest, spray_client)
		)
}
