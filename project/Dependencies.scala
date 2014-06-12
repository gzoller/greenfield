import sbt._

object Dependencies {

	val resolutionRepos = Seq(
		"Typesafe Repo" 			at "http://repo.typesafe.com/typesafe/releases/",
		"Scala Tools"				at "https://oss.sonatype.org/content/groups/scala-tools/",
		"OSS"						at "http://oss.sonatype.org/content/repositories/releases",
		"Spray" 					at "http://repo.spray.io",
		"Mvn"                       at "http://mvnrepository.com/artifact"  // for commons_exec
	)

	def xcompile  (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")  // 'xcompile' so as not to break mutli-jvm stuff
	def provided  (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")
	def test      (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test") 
	def runtime   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "runtime")
	def container (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "container")

	val SPRAY_VER 		= "1.3.1-20140423"
	val AKKA_VER 		= "2.3.3"

	val scalajack 		= "co.blocke"				%%"scalajack"			% "2.0.1"
	val akka_actor		= "com.typesafe.akka"		% "akka-actor_2.11"		% AKKA_VER
	val akka_slf4j 		= "com.typesafe.akka" 		% "akka-slf4j_2.11"		% AKKA_VER
	val akka_remote		= "com.typesafe.akka" 		% "akka-remote_2.11"	% AKKA_VER
	val akka_cluster	= "com.typesafe.akka" 		% "akka-cluster_2.11" 	% AKKA_VER
	val multijvm        = "com.typesafe.akka"       %% "akka-multi-node-testkit" % "2.3.2"  // 2.3.3 isn't ready yet!
	val spray_can		= "io.spray"				%% "spray-can" 			% SPRAY_VER
	val spray_client	= "io.spray"				%% "spray-client"		% SPRAY_VER
	val spray_routing	= "io.spray"				%% "spray-routing"		% SPRAY_VER
	val spray_caching	= "io.spray"				%% "spray-caching"		% SPRAY_VER
	val scalautils      = "org.scalautils" 			%% "scalautils"			% "2.1.7"

	// val commons_codec	= "commons-codec"			% "commons-codec"       % "1.8"
	// val commons_lang	= "commons-lang"			% "commons-lang"        % "2.6"
	// val commons_exec	= "org.apache.commons" 		% "commons-exec"        % "1.2"
	// val joda_time		= "joda-time"				% "joda-time"			% "2.3"
	// val logback 		= "ch.qos.logback" 			% "logback-classic"		% "1.0.11"
	// val mongo_java 		= "org.mongodb" 			% "mongo-java-driver" 	% "2.11.3"
	// val typesafe_config	= "com.typesafe"			% "config"				% "1.2.0"
	// val casbah 			= "org.mongodb" 			% "casbah_2.10"			% "2.6.3"
	// val casbah_gridfs	= "org.mongodb" 			% "casbah-gridfs_2.10"	% "2.6.3"
	// val parboiled       = "org.parboiled"			% "parboiled-scala_2.10"% "1.1.6"
	// val googleCLHM      = "com.googlecode.concurrentlinkedhashmap" % "concurrentlinkedhashmap-lru" % "1.4"

	// val prettytime		= "org.ocpsoft.prettytime"	% "prettytime"			% "3.2.1.Final"

	val scalatest 		= "org.scalatest" 			%% "scalatest"			% "2.2.0"
	val slf4j_simple 	= "org.slf4j" 				% "slf4j-simple" 		% "1.7.5"
}
