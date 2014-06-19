package co.nubilus
package roots

import akka.actor.Actor
import core.SetOnce

trait PodActor extends Actor {

	protected val pod : Pod

	def receive = {
		case "ping" => sender ! "pong (default) Pod "+pod.versions.mkString("[",", ","]")
	}
}

class DefaultPodActor( val pod:Pod ) extends PodActor