package co.nubilus
package roots

case class Threashhold(
	min     : Int,
	max     : Int,
	reading : Int
	)

trait Stat extends Any

case class IntStat        ( value:Int         ) extends AnyVal with Stat
case class DoubleStat     ( value:Double      ) extends AnyVal with Stat
case class StringStat     ( value:String      ) extends AnyVal with Stat
case class CounterStat    ( value:Long        ) extends AnyVal with Stat
case class BoolStat       ( value:Long        ) extends AnyVal with Stat
case class ThreashholdStat( value:Threashhold ) extends AnyVal with Stat

case class MeterReading[T <: Stat](
	when     : Long,
	reaiding : T
	)

case class HistroyStat[T <: Stat](
	readings : List[MeterReading[T]]
	) extends Stat 

trait Stats {
	def stats() : Map[String, Stat]  // stat_name -> Stat value
}
