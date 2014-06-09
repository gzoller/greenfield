package io.recurve

import com.typesafe.config.ConfigFactory

case class ServerContext( config:Config ) {
	private[recurve] val upSince            = new PosixDate()
	private[recurve] val node               = config getString      "node"
	private[recurve] val slot               = config getString      "slot"
	private[recurve] val homeAddr           = config getString      "home_addr"
	private[recurve] val nettyPort          = config getInt         "akka.remote.netty.tcp.port"
	private[recurve] val statusMeterType    = config getString      "status_meter_type" // e.g. io.recurve.status.LocalStatusMeter
	private[recurve] val statusCommand      = config getString      "status_command"

	private[recurve] val system = ActorSystem(clusterName, ConfigFactory.parseString("akka.cluster.roles = " 
			+ (pluginClasses.map(mname => "\"" + env + ":" + mname + "\"") :+ ("\"" + env + ":" + classOf[Plugin].getName + "\"")).mkString("[", ",", "]")).withFallback(config))

	private[recurve] val logger = system.log
	// def myHostname = InetAddress.getLocalHost.getHostAddress
	// val myHttpUri  = "http://" + myHostname + ":" + httpPort + "/"

	private[recurve] def hello = HelloMessage( node, slot )
}

trait Server {
	private lazy val context = ServerContext( ConfigFactory.load )

	def log    = context.logger
	def system = context.system

	private[recurve] var state : ServerStates.Value = ServerStates.Alive

	private[recurve] var box : Option[Box] = None  // we start off empty

	private[recurve] val cancelHello = system.scheduler.schedule(2 seconds, 15 seconds, system.actorSelection(context.homeAddr), context.hello)
}

object ServerStates extends Enumeration {
	val Alive, Assigned, Dying = Value
}

case class HelloMessage(
	node  : String,
	slot  : String
) extends RecurveMessage