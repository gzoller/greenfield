package co.nubilus
package roots

import java.util.Date

object HealthStatus extends Enumeration {
	val 	OK, 	// All good
			COLD, 	// Mildy sick.  No action likely needed.
			FLU, 	// Not well.  Not imperative, but needing some attention.
			CANCER 	// Critically ill.  Look into this immediately before something dies.
			= Value
}

// Allow reporting of various, named sub-components
case class HealthReport(
	vitals  : Map[ String, HealthStatus.Value ],
	whenTS  : Long = (new java.util.Date()).getTime
	)

trait HealthMonitor {
	def healthCheck() : HealthReport  // No default implementation.  Concrete classes must provide this.
}
