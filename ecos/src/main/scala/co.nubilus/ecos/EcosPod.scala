package co.nubilus
package ecos

import core._
import roots._
// import akka.actor.Props

class EcosPod() extends Pod {
	val name    = "ecos"
	val version = BuildInfo.version

	private[ecos] var health:HealthStatus.Value = HealthStatus.OK
	def healthCheck() = HealthReport( 
			Map("ecos" -> health)
		)

	def stats() = Map.empty[String,Stat]
}

// trait Ecos extends Server {
// 	val version = Version("platform:ecos",BuildInfo.version)
// 	system.actorOf( Props(new EcosActor(this)) )
// }

/*
// Messages
case class ErrorMsg( msg:String, host:String = Util.myHost )
case class PodMsg( version:Version, cfg:String )
*/

