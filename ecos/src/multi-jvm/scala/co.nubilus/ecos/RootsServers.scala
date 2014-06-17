package co.nubilus
package ecos

import roots._
import com.typesafe.config.ConfigFactory
import akka.actor._

case class PodServer( p:Int, r:String ) extends TestServerWorker { 
	val port = p; 
	val role = r; 

	// system has been defined as lazy in Roots to allow subclasses to define key vals and
	// possibly override digestConfig.  Now we must force resolution of the lazy system so that
	// the actor starts up.
	val s = system 
}
case class EcosServer( p:Int, r:String ) extends TestServerWorker { 
	val port = p
	val role = r
	// override val actor : Props = Props(new EcosTestActor(null,this))
	val s = system 
}

trait TestServerWorker extends Roots {
	val port : Int
	val role : String

	override def digestConfig(a:Props) = {
		val cfg      = ConfigFactory.parseString("akka.remote.netty.tcp.port = "+port+",akka.cluster.roles=["+role+"]").withFallback(ConfigFactory.load("worker.conf"))
		val system   = ActorSystem( "rootsCluster", cfg )
		system.actorOf( a, "roots" )
		system
	}
}

case class RootsStartMsg( port:Int, isPod:Boolean )
case class RootsStopMsg()
case class TestServerStopMsg()

trait TestServer {
	val port   : Int
	var roots  : Roots = null
	var system : ActorSystem = null

	def run() {
		val cfg = ConfigFactory.parseString("akka.remote.netty.tcp.port = "+port).withFallback(ConfigFactory.load("testServer.conf"))
		system  = ActorSystem( "test", cfg )
		system.actorOf( Props( new TestServerActor(this)), "test" )
		while( !system.isTerminated ) {
			Thread.sleep(500)
		}
	}
}

class TestServerActor( ts:TestServer ) extends Actor {
	def receive = {
		case "ping"                 => sender ! "pong test"

		case tssm:TestServerStopMsg => 
			println("Stopping "+ts.port)
			ts.system.shutdown

		case rsm:RootsStartMsg =>
			if( rsm.isPod ) {
				ts.roots = PodServer(rsm.port, "pod")
			}

		case rstp:RootsStopMsg => ts.roots.system.shutdown

		case other => println("Other: "+other)
	}
}
