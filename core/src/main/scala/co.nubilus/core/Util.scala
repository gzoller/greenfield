package co.nubilus
package core

import scala.util.Try
import java.io._
import akka.util.Timeout
import spray.can.Http
import spray.http._
import spray.httpx.RequestBuilding._
import HttpMethods._
import akka.io.IO
import akka.pattern.ask
import akka.actor.ActorSystem
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global


object Util {
	def myHost = java.net.InetAddress.getLocalHost.getHostAddress

	def materialize[T <: Serializable]( ba:Array[Byte] ) = {
		Try{
			val ois = new ObjectInputStream( new ByteArrayInputStream(ba) )
			val m = ois.readObject().asInstanceOf[T]
			ois.close
			m
		}.toOption
	}

	def loadClass[T]( className:String, cl:ClassLoader ) = Class.forName( className, true, cl ).newInstance().asInstanceOf[T]

	def httpGet( uri:String )(implicit s:ActorSystem, timeout:Timeout = 30 seconds) = {
		val resp = _http( Get(uri) )
		(resp.entity.asString, resp.status)
	}

	private def _http( hr:HttpRequest )(implicit s:ActorSystem, timeout:Timeout = 30 seconds) = {
		val response: Future[HttpResponse] = (IO(Http) ? hr).mapTo[HttpResponse]
		Await.result( response, Duration.Inf )
	}
}