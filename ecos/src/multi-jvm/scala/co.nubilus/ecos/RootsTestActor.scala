package co.nubilus
package roots

import ecos._
import akka.actor.Actor
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

case class RootsTestActor( r:Roots ) extends RootsActor(r) {
	def rec2 : Actor.Receive = {
		case w:WhoDoYouKnow => sender ! Friends( r.ecosUris.toSet, Set[String]() )
	}

	override def receive = super.receive orElse rec2	
}