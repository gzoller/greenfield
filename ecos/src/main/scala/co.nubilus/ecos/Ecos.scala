package co.nubilus
package ecos

import roots._
import com.typesafe.config.ConfigFactory
import akka.actor.{Actor, ActorSystem, Props}

object Ecos extends App {

	case class EcosServer() extends Roots {
		println("----------------------------------------")
		println( "Ecos Server" )
		println( "version "+this.version.ver)
		println( "Started!" )
		println("----------------------------------------")

		setPod(new EcosPod(), List(leaves.EcosLeafv1()))

		override val actor = Props(new EcosRootsActor( pod.asInstanceOf[EcosPod], this ))
	}
	val server = EcosServer()

}