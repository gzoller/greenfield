package co.nubilus
package roots

case class RootsDescriptor(
	nodeId   : String,
	ip       : String,
	cores    : Int,
	memoryMB : Int,
	diskMB   : Int,
	pod      : Option[Version]
	)