package co.nubilus
package ecos

import roots._
import com.typesafe.config.ConfigFactory
import akka.actor._

case class PodServer( p:Int, r:String ) extends TestServerWorker { 
	val port = p; 
	val role = r; 

	override val actor : Props = Props(new RootsTestActor(this))

	init(cfg)
}

case class EcosServer( p:Int, r:String ) extends TestServerWorker { 
	val port = p
	val role = r
	override val actor : Props = Props(new EcosTestActor(null,this))

	init(cfg)
}

trait TestServerWorker extends Roots {
	val port : Int
	val role : String

	def cfg() = ConfigFactory.parseString(s"""
		akka.remote.netty.tcp.port = $port
		akka.cluster.roles =[ $role ]
		""").withFallback(ConfigFactory.load("worker.conf"))
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
			ts.system.shutdown

		case rsm:RootsStartMsg      =>
			if( rsm.isPod )
				ts.roots = PodServer(rsm.port, "pod")
			else
				ts.roots = EcosServer(rsm.port, "ecos")

		case rstp:RootsStopMsg      => 
			akka.cluster.Cluster(ts.roots.system).leave(Address("akka.tcp","rootsCluster", core.Util.myHost, ts.roots.asInstanceOf[TestServerWorker].port))
			Thread.sleep(2000)
			ts.roots.system.shutdown

		case other => println("Other: "+other)
	}
}