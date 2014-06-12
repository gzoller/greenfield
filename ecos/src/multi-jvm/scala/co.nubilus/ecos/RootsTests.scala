package co.nubilus
package ecos

import roots._
import org.scalatest._
import org.scalatest.Matchers._
import akka.actor.ActorSelection
import akka.pattern.ask
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import akka.util.Timeout

class RootsMultiJvmTests1 extends FunSpec with BeforeAndAfterAll {

	var t : Roots                = null
	val host                     = core.Util.myHost
	val akkaUri                  = s"""akka.tcp://roots@$host:9002/user/b"""
	var selection:ActorSelection = null
	implicit val to:Timeout      = 15.seconds

	override def beforeAll() {
		t = TestRoots()
		selection = t.system.actorSelection( akkaUri )
		Thread.sleep(500)
	}

	// When we're done, send the stop message to kill the Ecos server.
	override def afterAll() {
		selection ! "stop"
	}

	describe("===============\n| Roots Tests |\n===============") {
		it("Environment starts up and responds to ping") {
			val resp = selection ? "yin"
			val reply = Await.result(resp, 15.seconds)
			reply should be("yang")
		}
	}
}

class RootsMultiJvmTests2 extends FunSpec {
	describe("::: Ecos Node :::") {
		it("Environment starts up") {
			val t = TestRoots2()
			while( !t.system.isTerminated ) {
				Thread.sleep(500)
			}
		}
	}
}
