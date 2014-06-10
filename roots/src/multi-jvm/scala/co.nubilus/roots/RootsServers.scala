package co.nubilus
package roots

import com.typesafe.config.ConfigFactory
import akka.actor._

case class TestRoots() extends Roots {
	override def digestConfig() = {
		val cfg      = ConfigFactory.load("roots1.conf")
		val system   = ActorSystem( "roots", cfg )
		system.actorOf( Props(new RootsActor(this)), "a" )
		val ref      = null 
		(system, ref)
	}
}

case class TestRoots2() extends Roots {
	override def digestConfig() = {
		val cfg      = ConfigFactory.load("roots2.conf")
		val system   = ActorSystem( "roots", cfg )
		system.actorOf( Props(new TestEcosActor(this)), "b" )
		val ref      = null
		(system, ref)
	}
}