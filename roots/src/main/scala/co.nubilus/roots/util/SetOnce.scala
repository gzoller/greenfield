package co.nubilus
package roots.util

import annotation._
import scala.language.implicitConversions

/**
	Handly little something from here: 
		http://stackoverflow.com/questions/4404024/how-to-simulate-an-assign-once-var-in-scala/4407534#4407534
	This allows you to set a value just once, then never again.  Perfect for a lot of config values so
	you don't need a lot of Options, etc., to simulate set-once behavior.
 */

class SetOnce[T] {
	private[this] var value: Option[T] = None

	def isSet = value.isDefined
	def ensureSet { if (value.isEmpty) throwISE("uninitialized value") }
	def apply() = { ensureSet; value.get }

	def :=(finalValue: T)(implicit credential: SetOnceCredential) {
		value = Some(finalValue)
	}
	def allowAssignment = {
		if (value.isDefined) throwISE("final value already set")
			else new SetOnceCredential
	}
	private def throwISE(msg: String) = throw new IllegalStateException(msg)

	@implicitNotFound(msg = "This value cannot be assigned without the proper credential token.")
	class SetOnceCredential private[SetOnce]
}

object SetOnce {
	implicit def unwrap[A](wrapped: SetOnce[A]): A = wrapped()
}