package co.nubilus
package ecos

import roots._
import akka.actor.{Actor, Address}
import akka.cluster.{Member, MemberStatus, Cluster}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberRemoved, MemberUp}

class EcosRootsActor( ecos:EcosPod, r:Roots ) extends RootsActor(r) {

	private[ecos] var podNodes  = Set.empty[String]
	private[ecos] var ecosNodes = Set.empty[String]

	val cluster = Cluster(context.system)

	// subscribe to cluster events
	override def preStart() = {
		cluster.subscribe(self, classOf[MemberUp])
		cluster.subscribe(self, classOf[MemberRemoved])
	}
	override def postStop() = cluster.unsubscribe(self)

	override def receive = super.receive orElse rec2

	private def fullAddr( a:Address ) : String = a.toString + "/user/roots"
	private def sayToPods( m:Any ) = podNodes.foreach( p => r.system.actorSelection( p ) ! m )

	private def rec2() : Actor.Receive = {

		// Cluster memebership event listeners
		//-------------------------------------

		// Event sent when this actor first comes up. Register all existing cluster members (nodes)
		case state : CurrentClusterState => {
			val (en, pn) = state.members.collect {
				case m if m.status == MemberStatus.Up => m
			}.partition( _.roles.contains("ecos"))
			ecosNodes = en.map( a => fullAddr(a.address) )
			podNodes = pn.map( a => fullAddr(a.address) )
		}

		// Event sent when a new cluster member comes up. Register the new cluster member if it is the parent node
//akka.tcp://rootsCluster@10.23.1.188:9001
		case MemberUp(member) => 
// println("Member Up: "+member.address+"  Roles: "+member.roles)
			if( member.roles.contains("ecos") ) {
				ecosNodes += fullAddr(member.address)
				sayToPods( EcosMsg( ecosNodes ) ) // Now tell all the pods about the new ecos
			} else {
				val pa = fullAddr( member.address )
				podNodes += pa
				// New pod node--tell it about ecos nodes
				r.system.actorSelection( pa ) ! EcosMsg( ecosNodes )
			}

		// Event sent when a cluster member is removed. Unregister the cluster member if it is the parent node
		case MemberRemoved(member, previousStatus) => 
			if( member.roles.contains("ecos") ) {
				ecosNodes -= fullAddr( member.address )
				sayToPods( EcosMsg( ecosNodes ) )  // tell pods we've lost an ecos node
			} else 
				podNodes -= fullAddr( member.address )
	}
}