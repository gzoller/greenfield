package co.nubilus
package roots

import spray.routing._

trait Leaf {
	val name    : String
	val version : String
	val route   : RequestContext => Unit

	def init( r:Roots, p:Pod )
}

// trait SvcApi extends ApiEndpoints with Cacheable {
//         val dsc:DataSourceConfig
// }