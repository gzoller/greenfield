package co.nubilus
package ecos

import core._
import akka.actor.Props

trait Ecos extends Server {
	val version = Version("platform:ecos",BuildInfo.version)
	system.actorOf( Props(new EcosActor(this)) )
}

/*
// Messages
case class ErrorMsg( msg:String, host:String = Util.myHost )
case class PodMsg( version:Version, cfg:String )
*/

class EcosActor( ecos:Ecos ) extends Actor {

	def receive = {

		case "ping" => sender ! "pong"

	}
}
