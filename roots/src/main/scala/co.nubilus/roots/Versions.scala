package co.nubilus
package roots

case class Version(
	name : String,
	ver  : String
) {
	override def toString() = s"""$name/$ver"""
}

trait Versions {
	def versions() : List[Version]
}