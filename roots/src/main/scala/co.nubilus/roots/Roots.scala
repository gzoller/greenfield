package co.nubilus
package roots

import core._
import com.typesafe.config.{Config, ConfigFactory}
import akka.actor._

trait Roots {
	val version        = Version("roots:roots",BuildInfo.version)
	val actor : Props  = Props(new RootsActor(this))
	final val system   = new SetOnce[ActorSystem]
	// final val port     = new SetOnce[Int]

	protected final val pod = new SetOnce[Pod]
	def podVersion = 
		if( pod.isSet )
			Version(pod.name,pod.version)
		else
			Version("none","none")

	private[roots] var health   : HealthStatus.Value     = HealthStatus.OK
	private[roots] var ecosUris : List[String]           = List.empty[String]

	// All done for now... we just wait for Ecos to notify us with instructions to load a Pod.
	// Meanwhile we can answer all events.

	def init( cfg:Config ) = {
		implicit val systemCred = system.allowAssignment 
		// implicit val portCred   = port.allowAssignment 
		system := ActorSystem( cfg.getString("cluster-name"), cfg )
		system.actorOf( actor, "roots" )
		// port := cfg.getInt("akka.remote.netty.tcp.port")
	}

	def setPod( p:Pod, lvs:List[Leaf] ) : Boolean = 
		if( pod.isSet ) {
			if( Version(pod.name,pod.version) != Version(p.name,p.version)) false
			else return true // else no action but ok (pod already set to same version as requested set)
		} else {
			implicit val podCred = pod.allowAssignment
			pod := p
			p.init( this, lvs )
			true
		}
}
