package co.nubilus
package roots

/**
	Lifecycle event callbacks.  These are no-op by default.  Implementations can override to taste.
 */
trait Lifecycle {
	private[roots] def init( r:Roots )

	def preStartup( r:Roots )  {}
	def postStartup( r:Roots ) {}

	def preStop( r:Roots )     {}
	def postStop( r:Roots )    {}
}