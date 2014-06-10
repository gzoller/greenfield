package co.nubilus
package core

import scala.util.Try
import java.io._

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
}