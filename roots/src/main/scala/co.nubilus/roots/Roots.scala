package co.nubilus
package roots

import core._
import com.typesafe.config.ConfigFactory
import akka.actor.{Actor, ActorSystem, Props}
import java.net.{URL, URLClassLoader}
import scala.collection.JavaConversions._
import scala.util.Try

trait Roots {
	val version = Version("roots:roots",BuildInfo.version)
	val (system, ecosRef) = digestConfig()

	private[roots] final val pod = new SetOnce[Pod]

	private[roots] var health:HealthStatus.Value = HealthStatus.OK

	// All done for now... we just wait for Ecos to notify us with instructions to load a Pod.
	// Meanwhile we can answer all events.

	def digestConfig() = {
		val cfg      = ConfigFactory.load
		val system   = ActorSystem( cfg.getString("cluster-name"), cfg )
		system.actorOf( Props(new RootsActor( this )), "roots" )
		val ref      = system.actorSelection( cfg.getString("ecos-uri" ) )
		(system, ref)
	}

	def setPod( p:Pod, lvs:List[Leaf] ) = 
		if( pod.isSet ) false
		else {
			implicit val podCred = pod.allowAssignment
			pod := p
			p.init( this, lvs )
			true
		}
}

// Messages
case class ErrorMsg( msg:String, host:String = Util.myHost )
case class PodMsg( version:Version, cfg:String )

class RootsActor( roots:Roots ) extends Actor {

	def receive = {

		case pm:PodMsg => 
			Try( ConfigFactory.load( ConfigFactory.parseString(pm.cfg) ) ).toOption.fold({
				roots.health = HealthStatus.CANCER
				sender ! ErrorMsg("Could not read configuration for Pod "+pm.version.name+"/"+pm.version.ver) 
			})( config =>
				Try(
					new URLClassLoader( config.getStringList("jar-files").map( u => new URL(u) ).toArray )
				).toOption.fold({
					roots.health = HealthStatus.CANCER
					sender ! ErrorMsg("Could not create URLClassLoader for Pod "+pm.version.name+"/"+pm.version.ver) 
				})( ucl => 
					Try{
						val p = Util.loadClass[Pod]( config.getString("pod-class"), ucl )
						val lvs = config.getStringList("leaf-classes").map( lc => Util.loadClass[Leaf]( lc, ucl )).toList
						(p,lvs)
					}.toOption.fold({
						roots.health = HealthStatus.CANCER
						sender ! ErrorMsg("Could not marshal Pod or Leaf classes for Pod "+pm.version.name+"/"+pm.version.ver) 
					}){ case(p,lvs) =>
						if( !roots.setPod(p, lvs) ) 
							sender ! ErrorMsg("Attempted to set Pod "+p.name+"/"+p.version+" but Pod "+roots.pod.name+"/"+roots.pod.version+" was already set.")
						else
							p.start( config )
					}
				)
			)

		// case "ping" => 	println("Ping received!")
	}
}