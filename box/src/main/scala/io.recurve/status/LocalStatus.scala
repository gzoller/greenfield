package io.recurve
package status

case class LocalStatus(
	cpuPctUsed  : List[Option[Int]],
	memoryUsed  : List[Option[Int]],
	memoryAvail : Int,
	networkUsed : List[Option[Int]],
) extends EnvStatus

case class LocalStatusMeter() {
	private cpu     = FloatingHistogram[Int]()
	private memory  = FloatingHistogram[Int]()
	private network = FloatingHistogram[Int]()

	def readMetrics = {
	}

	def getMetrics() = LocalStatus(cpu.entries, memory.entries, 0, network.entries)
}

// 12 readings / hr  (every 5 min)
// 24 hr / day
// = 288 data points/day

//ps -p <pid> -o %cpu,%mem,cmd  -- may not be accurate!

// top -pid 30563