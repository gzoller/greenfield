package co.nubilus
package ecos

import roots._
import akka.actor.Actor
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class EcosTestActor( ecos:EcosPod, roots:Roots ) extends EcosRootsActor(ecos, roots) {

	def rec2 : Actor.Receive = {
		case "yin" => sender ! "yang"  //println("Ping received!!!")
		case "stop" => roots.system.shutdown
	}

	override def receive = super.receive orElse rec2
}