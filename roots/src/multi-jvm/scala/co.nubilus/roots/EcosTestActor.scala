package co.nubilus
package roots

import akka.actor.Actor

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class TestEcosActor( roots:Roots ) extends RootsActor(roots) {

	def rec2 : Actor.Receive = {
		case "ping" => sender ! "pong"  //println("Ping received!!!")
		case "stop" => roots.system.shutdown
	}

	def recAll = List(super.receive, rec2)

	override def receive = recAll.reduceLeft { (a,b) => a orElse b }
}