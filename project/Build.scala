import sbt._
import sbt.Keys._

import sbtassembly.Plugin.AssemblyKeys._
import sbtbuildinfo.Plugin._
import scala.Some

import com.typesafe.sbt.SbtMultiJvm
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.{MultiJvm, jvmOptions}

object Build extends Build {

import Dependencies._

	val myHost = java.net.InetAddress.getLocalHost.getHostAddress

	lazy val basicSettings = Seq(
		scalaVersion 				:= "2.11.1",
		resolvers ++= Dependencies.resolutionRepos,
		scalacOptions				:= Seq(
										"-feature", 
										"-Xlint",
										"-deprecation", 
										"-encoding", "UTF8", 
										"-unchecked"),
		assembleArtifact in packageScala := false,
		testOptions in Test 		+= Tests.Argument("-oDF"),
		version 					:= "0.1.0"

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
		buildInfoPackage := "co.nubilus"
		)

	lazy val multiJvmSettings = SbtMultiJvm.multiJvmSettings ++ Seq(
		// make sure that MultiJvm test are compiled by the default test compilation
		compile in MultiJvm <<= (compile in MultiJvm) triggeredBy (compile in Test),
		// disable parallel tests
		parallelExecution in Test := false,
		// make sure that MultiJvm tests are executed by the default test target
		executeTests in Test <<= (executeTests in Test, executeTests in MultiJvm) map {
				case (testResults, multiNodeResults) =>
					val overall =
						if( testResults.overall.id< multiNodeResults.overall.id )
							multiNodeResults.overall
						else
							testResults.overall
					Tests.Output(overall,
							testResults.events ++ multiNodeResults.events,
							testResults.summaries ++ multiNodeResults.summaries)
			}
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

	lazy val core = Project(
		"core", 
		file("core"),
		settings = basicSettings ++ (libraryDependencies ++=
			xcompile(akka_actor, spray_routing, spray_can, spray_client) ++
			test(scalatest)
		)
	)

	lazy val roots = Project(
		"roots", 
		file("roots"),
		settings = basicSettings ++ buildSettings 
			++ (libraryDependencies ++=
				xcompile(scalautils, akka_actor, spray_routing, spray_can, akka_cluster) ++
				test(scalatest, spray_client, akka_cluster, akka_slf4j, slf4j_simple)
			)
	).dependsOn( core )

	lazy val ecos = Project(
		"ecos", 
		file("ecos"),
		settings = basicSettings ++ buildSettings ++ multiJvmSettings  
			++ Seq(
				jvmOptions in MultiJvm += "-DmyIp="+myHost
				)
			++ (libraryDependencies ++=
				xcompile(akka_actor, spray_routing, spray_can) ++
				test(scalatest, multijvm, akka_cluster, akka_slf4j, slf4j_simple)
			),
		configurations = Configurations.default :+ MultiJvm
	).dependsOn( core, roots )
}
