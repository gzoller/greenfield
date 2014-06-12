package co.nubilus
package ecos

import roots._
import akka.actor.{Actor, Address}
import akka.cluster.{Member, MemberStatus, Cluster}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberRemoved, MemberUp}

class EcosRootsActor( ecos:EcosPod, r:Roots ) extends RootsActor(r) {

	private[ecos] var nodes = Set.empty[Address]

	override def receive = super.receive orElse rec2

	private def rec2 : Actor.Receive = {

		// Cluster memebership event listeners
		//-------------------------------------

		// Event sent when this actor first comes up. Register all existing cluster members (nodes)
		case state   : CurrentClusterState =>
			nodes = state.members.collect {
				case m if m.status == MemberStatus.Up => m.address
			}

		// Event sent when a new cluster member comes up. Register the new cluster member if it is the parent node
		case state   : MemberUp            => nodes += state.member.address

		// Event sent when a cluster member is removed. Unregister the cluster member if it is the parent node
		case state   : MemberRemoved       => nodes -= state.member.address
	}
}