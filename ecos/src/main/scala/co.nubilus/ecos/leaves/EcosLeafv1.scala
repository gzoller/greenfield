package co.nubilus
package ecos
package leaves

import roots._
import scala.concurrent.Future
import spray.http.MediaTypes._
import spray.routing._
import Directives._
import scala.concurrent.ExecutionContext.Implicits.global

case class EcosLeafv1() extends Leaf {
	val name    = "EcosAPI"
	val version = "1"

	val route = 
			respondWithMediaType(`application/json`) {
				get {
					path( "ping" ) { 
						complete{ Future( """{"pong":1}""")  }
					}
				}
			}

	def init( r:Roots, p:Pod ){}
}