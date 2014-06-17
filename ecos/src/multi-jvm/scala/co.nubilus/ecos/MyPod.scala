package co.nubilus
package ecos

import roots._

class MyPod() extends Pod {
	val name = "MyPod"
	val version = "1"

	def healthCheck() : HealthReport = HealthReport( Map.empty[String,HealthStatus.Value] )
	def stats() : Map[String, Stat] = Map.empty[String,Stat]
}