package co.nubilus
package ecos

import roots._
import akka.actor.Actor
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

case class WhoDoYouKnow()
case class Friends( eocs:Set[String], pods:Set[String] )

class EcosTestActor( ecos:EcosPod, roots:Roots ) extends EcosRootsActor(ecos, roots) {

	def rec2 : Actor.Receive = {
		case "yin"          => sender ! "yang" 
		case w:WhoDoYouKnow => sender ! Friends( ecosNodes, podNodes )
		case "stop"         => roots.system.shutdown
		case x => println("Other: "+x)
	}

	override def receive = super.receive orElse rec2
}