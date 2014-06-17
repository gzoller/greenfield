package co.nubilus
package roots

import core._
import com.typesafe.config.{Config, ConfigFactory}
import akka.actor._

trait Roots {
	val version        = Version("roots:roots",BuildInfo.version)
	val actor : Props  = Props(new RootsActor(this))
	lazy val system    = digestConfig(actor)

	protected final val pod = new SetOnce[Pod]
	def podVersion = 
		if( pod.isSet )
			Version(pod.name,pod.version)
		else
			Version("none","none")

	private[roots] var health  : HealthStatus.Value     = HealthStatus.OK
	private[roots] var ecosUri : Option[String]         = None
	private[roots] var ecosRef : Option[ActorSelection] = None

	// All done for now... we just wait for Ecos to notify us with instructions to load a Pod.
	// Meanwhile we can answer all events.

	def digestConfig(a:Props) = {
		val cfg      = ConfigFactory.load
		val system   = ActorSystem( cfg.getString("cluster-name"), cfg )
		system.actorOf( a, "roots" )
		system
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
