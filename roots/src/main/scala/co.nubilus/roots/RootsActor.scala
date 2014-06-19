package co.nubilus
package roots

import core.Util
import java.net.{URL, URLClassLoader}
import akka.actor.Actor
import scala.util.Try
import org.scalautils._
import com.typesafe.config._
import scala.collection.JavaConversions._

// Messages
case class ErrorMsg( msg:String, host:String = Util.myHost )
case class PodMsg( version:Version, cfg:String )
case class EcosMsg( ecosInstances:Set[String] )
case class VerMsg()

/*
	Pod config fields:
		jar-files
		pod-class
		leaf-classes
		...others...
 */

class RootsActor( roots:Roots ) extends Actor {

	def receive = {
		case "ping"     => sender ! "pong"
		case vm:VerMsg  => sender ! List(roots.version, roots.podVersion)
		case em:EcosMsg => 
			roots.ecosUris = scala.util.Random.shuffle( em.ecosInstances.toList )
			// println("Ecos discovered: "+roots.ecosUris)
			// println("Sender: "+sender.path.address)
		case pm:PodMsg  => loadPod( pm )
	}

	def loadPod( pm : PodMsg ) = {
		def readConfig() : Config Or (Option[HealthStatus.Value],ErrorMsg) = 
			Try( Good(ConfigFactory.load( ConfigFactory.parseString(pm.cfg) )) ).toOption
				.getOrElse( Bad((Some(HealthStatus.CANCER), ErrorMsg("Could not read configuration for Pod "+pm.version)) ))

		def getClassLoader( config:Config ) : URLClassLoader Or (Option[HealthStatus.Value],ErrorMsg) =
			Try( Good(new URLClassLoader( config.getStringList("jar-files").map( u => new URL(u) ).toArray ))).toOption
				.getOrElse( Bad((Some(HealthStatus.CANCER), ErrorMsg("Could not create URLClassLoader for Pod "+pm.version)) ))

		def loadClasses( ucl:URLClassLoader, config:Config ) : (Pod, List[Leaf]) Or (Option[HealthStatus.Value],ErrorMsg) = 
			Try {
				val p   = Util.loadClass[Pod]( config.getString("pod-class"), ucl )
				val lvs = config.getStringList("leaf-classes").map( lc => Util.loadClass[Leaf]( lc, ucl )).toList
				Good( (p,lvs) )
			}.toOption
				.getOrElse( Bad((Some(HealthStatus.CANCER), ErrorMsg("Could not marshal Pod or Leaf classes for Pod "+pm.version)) ))

		def startPod( p:Pod, lvs:List[Leaf], c:Config ) : Unit Or (Option[HealthStatus.Value],ErrorMsg) =
			if( roots.setPod(p, lvs) ) Good(p.start(c)) else Bad((None, ErrorMsg("Attempted to set Pod "+Version(p.name,p.version)+" but Pod "+roots.podVersion+" was already set.")))

		// Step thru each process step. An error will yield Bad if any step fails.  Cleaner alternative to nested if's or fold's.
		(for {
			cfg  <- readConfig
			ucl  <- getClassLoader(cfg)
			t    <- loadClasses(ucl,cfg)
			done <- startPod(t._1, t._2, cfg)
		} yield done) match {
			case Bad((health, errmsg)) =>
				health.map( h => roots.health = h )
				println("ERR: "+errmsg)
				sender ! errmsg
			case _ => // do nothing...worked
		}
	}
}