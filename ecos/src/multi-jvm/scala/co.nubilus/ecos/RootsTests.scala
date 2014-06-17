package co.nubilus
package ecos

import roots._
import core._
import org.scalatest._
import org.scalatest.Matchers._
import com.typesafe.config.{Config, ConfigFactory}
import akka.actor._
import akka.pattern.ask
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import akka.util.Timeout
import scala.util.Try
import sys.process._
import scala.language.postfixOps
import scala.util.control.Breaks._
import org.scalautils.TimesOnInt._

class RootsMultiJvmTests1 extends FunSpec with BeforeAndAfterAll {

	var t : Roots           = null
	val host                = core.Util.myHost
	implicit val to:Timeout = 15.seconds
	val cfg                 = ConfigFactory.parseString("akka.remote.netty.tcp.port = 9000").withFallback(ConfigFactory.load("testServer.conf"))
	implicit val system     = ActorSystem( "test", cfg )

	val delay               = 750

	// Http server for things like jar service
	val http = new HttpServer(system) //.init()

	private def sel(port:Int)      = system.actorSelection(s"""akka.tcp://test@$host:$port/user/test""")
	private def selRoots(port:Int) = system.actorSelection(s"""akka.tcp://rootsCluster@$host:$port/user/roots""")

	override def beforeAll() {
		// t = TestRootsPod1()
		// selection = t.system.actorSelection( akkaUri )
		Thread.sleep(3000)  // Allow time for all nodes/cluster to come up
	}

	// When we're done, send the stop message to kill the Ecos server.
	override def afterAll() = {
		(9001 to 9004).toStream.foreach( port => sel(port) ! TestServerStopMsg() )
		Thread.sleep(2000)
	}

	describe("===============\n| Roots Tests |\n===============") {
/*
		it("Environment starts up and responds to ping") {
			val reply = Await.result( sel(9001) ? "ping", 5.seconds )
			reply should be("pong test")
		}
		it("Starts, tests, and stops a Roots node a couple of times") {
			sel(9001) ! RootsStartMsg(9010, true)
			Thread.sleep(delay)
			val reply = Await.result( selRoots(9010) ? "ping", 5.seconds )
			reply should be("pong")
			sel(9001) ! RootsStopMsg()
			Thread.sleep(delay)
			Try( Await.result( selRoots(9010) ? "ping", 5.seconds ) ).isSuccess should be( false )
			sel(9001) ! RootsStartMsg(9010, true)
			Thread.sleep(delay)
			val reply2 = Await.result( selRoots(9010) ? "ping", 5.seconds )
			reply2 should be("pong")
			sel(9001) ! RootsStopMsg()
			Thread.sleep(delay)
		}
		*/
		it("Starts a Roots node and tells it to load a Pod.  Can ping the Pod") {
			sel(9001) ! RootsStartMsg(9010, true)
			Thread.sleep(delay)
			val reply = Await.result( selRoots(9010) ? VerMsg(), 5.seconds )
			reply should equal( List(Version("roots:roots","0.1.0"), Version("none","none")) )
			// Build a jar file for test Pod
			"jar cf ecos/src/test/resources/pod1.jar ecos/target/scala-2.11/multi-jvm-classes/co/nubilus/ecos/MyPod.class" !
			val cfg = s"""
				cluster-name = "podNest"
				http-port = 9030
				jar-files = ["http://localhost:9090/repo/pod1.jar"]
				pod-class = "co.nubilus.ecos.MyPod"
				leaf-classes = []
				akka {
					log-dead-letters-during-shutdown = off
					loglevel = "ERROR"
					stdout-loglevel = "ERROR"
					loggers = ["akka.event.slf4j.Slf4jLogger"]
					actor {
						provider = "akka.cluster.ClusterActorRefProvider"
					}
					remote {
						log-remote-lifecycle-events = off
						enabled-transports = ["akka.remote.netty.tcp"]
						netty.tcp {	
							port = 9040
						}
					}
					cluster {
						seed-nodes = [ "akka.tcp://podNest@$host:9040" ]
						auto-down = on
						log-info = off
					}
				}
			"""
			selRoots(9010) ! PodMsg( Version("MyPod","1"), cfg )
			Thread.sleep(delay)
			val reply2 = Await.result( selRoots(9010) ? VerMsg(), 5.seconds )
			println(reply2)
		}
		it("Successfully discovers new pod node") {
			(pending)
			/*
			Need to fire this up in yet another jvm!
			val t2 = TestRootsPod()
			// Figure out how to know if:
			// 1) Ecos registered new pod coming up
			// 2) new pod received a EcosMsg from Ecos
			breakable {
				10 times {
					val resp = selection ? WhoDoYouKnow()
					val reply = Await.result(resp, 15.seconds)
					val friends = reply.asInstanceOf[Friends]
					println(friends)
					if( friends.pods.size == 2 && friends.pods.contains(s"""akka.tcp://rootsCluster@$host:9003/user/roots""") ) break
					Thread.sleep(500)
				}
				fail("Didn't see new pod registered in ecos.")
			}
			*/
		}
		it("Successfully discovers new ecos node") {
			(pending)
		}
		it("Successfully removes ecos node") {
			(pending)
		}
		it("Successfully removes pod node") {
			(pending)
		}
	}
}

// This are then empty "shell" test servers.  They will be told to start/stop hosted Roots servers with
// their attending Pods or Ecos instances.
//-------------------------------------------------------

class RootsMultiJvmTests2() extends FunSpec with TestServer with BeforeAndAfterAll{ 
	val port : Int = 9001
	describe("server") {
		it("serves"){}
	}
	override def afterAll() {
		run
	}
}
class RootsMultiJvmTests3() extends FunSpec with TestServer with BeforeAndAfterAll{ 
	val port : Int = 9002
	describe("server") {
		it("serves"){}
	}
	override def afterAll() {
		run
	}
}
class RootsMultiJvmTests4() extends FunSpec with TestServer with BeforeAndAfterAll{ 
	val port : Int = 9003
	describe("server") {
		it("serves"){}
	}
	override def afterAll() {
		run
	}
}
class RootsMultiJvmTests5() extends FunSpec with TestServer with BeforeAndAfterAll{ 
	val port : Int = 9004
	describe("server") {
		it("serves"){}
	}
	override def afterAll() {
		run
	}
}



