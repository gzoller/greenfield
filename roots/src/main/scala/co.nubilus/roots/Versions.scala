package co.nubilus
package roots

case class Version(
	name : String,
	ver  : String
	)

trait Versions {
	def versions() : List[Version]
}