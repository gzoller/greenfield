package co.nubilus
package ecos

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._
import scala.language.postfixOps // compiler-recommeneded import
import java.net.InetAddress
import spray.http.MediaTypes._
import spray.routing._
import Directives._
import spray.http._
import HttpCharsets._
import akka.actor.ActorSystem

import sys.process._
import scala.language.postfixOps

class HttpServer(sys:ActorSystem) extends SimpleRoutingApp { 

	implicit val system = sys

	val route =
		get {
			respondWithMediaType(`application/java-archive`) {
				path( "repo" / Segment ) { (filename) =>
					complete {
						val fis = new java.io.FileInputStream("ecos/src/test/resources/"+filename)
						Stream.continually(fis.read).takeWhile(-1 !=).toArray.map(_.toByte)
					}
				}
			}
			// respondWithMediaType(`text/plain`) {
			// 	path( "pwd" ) {
			// 		complete{ 
			// 			"pwd".!!
			// 		} 
			// 	}
			// }
			/*
			respondWithMediaType(`application/json`) {
				//?form=json&range=1-100&byUpdated=<iso8601_date>~
				path( "oxy" ) { 
					parameters("range") { range =>
						range match {
							case "1-100"   => complete{ scala.io.Source.fromFile("src/test/resources/oxy.1","utf-8").mkString } 
							case "101-200" => complete{ scala.io.Source.fromFile("src/test/resources/oxy.2","utf-8").mkString } 
							case "201-300" => complete{ scala.io.Source.fromFile("src/test/resources/oxy.3","utf-8").mkString } 
							case "301-400" => complete{ scala.io.Source.fromFile("src/test/resources/oxy.4","utf-8").mkString } 
						}
					}
				} ~
			}
			*/
		}

	// def init()(implicit system:ActorSystem) { 
		Await.result(startServer( interface="localhost", port=9090 )( route ), Duration.Inf) 
	// }
}
