package co.nubilus
package roots

import core._
import spray.routing._
import Directives._
import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.Await
import scala.concurrent.duration._

trait Pod extends Lifecycle with HealthMonitor with Stats with Versions with SimpleRoutingApp with Serializable {

	val name    : String
	val version : String

	protected      final val roots   = new SetOnce[Roots]
	private        final val leaves  = new SetOnce[List[Leaf]]
	private[roots] final val started = new SetOnce[Boolean]
	protected      final val system  = new SetOnce[ActorSystem]

	private[roots] def init( r:Roots, lvs:List[Leaf] ) {
		// Credentials for set-once values
		implicit val rootsCred  = roots.allowAssignment  // credentials allowing setting of roots
		implicit val leavesCred = leaves.allowAssignment // credentials allowing setting of leaves
		roots  := r
		leaves := lvs
		preStartup( r )
	}

	private[roots] def start( config:Config ) = {

		// Start an ActorSystem if needed.  Don't use roots' ActorSystem!  That's only for low-level communication.
		implicit val systemCred = system.allowAssignment 
		system := ActorSystem( config.getString("cluster-name"), config )

		// Start Spray HTTP endpoint service
		implicit val sys:ActorSystem = system
		Await.result( 
			startServer( 
				interface = Util.myHost, 
				port = config.getInt("http-port"), 
				serviceActorName="pod" )( leaves.map(_.route).reduceLeft(_ ~ _) ), 
			Duration.Inf)

		postStartup( roots )
	}

    def versions() : List[Version] = {
    	//  Root ver  :: Pod ver                      :: Leaf vers
    	roots.version :: Version("pod:"+name,version) :: leaves().map(lf => Version("leaf:"+lf.name,lf.version))
    }
}

