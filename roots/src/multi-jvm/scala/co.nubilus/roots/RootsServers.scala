package co.nubilus
package roots

import com.typesafe.config.ConfigFactory
import akka.actor._

case class TestRoots() extends Roots {
	override def digestConfig(a:Props) = {
		val cfg      = ConfigFactory.load("roots1.conf")
		val system   = ActorSystem( "roots", cfg )
		system.actorOf( a, "a" )
		val ref      = null 
		(system, ref)
	}
}

case class TestRoots2() extends Roots {
	override val actor : Props = Props(new TestEcosActor(this))
	override def digestConfig(a:Props) = {
		val cfg      = ConfigFactory.load("roots2.conf")
		val system   = ActorSystem( "roots", cfg )
		system.actorOf( a, "b" )
		val ref      = null
		(system, ref)
	}
}