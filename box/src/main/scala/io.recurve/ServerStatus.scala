package io.recurve

case class Status(
	server : ServerStatus,      // Server status
	box    : BoxStatus,         // Box status
	env    : EnvStatus          // Environment status (CPU, memory, etc.)
)

case class ServerStatus(
	state   : ServerStates.Value,
	upSince : PosixDate,
	version : String = BuildInfo.version
)

trait BoxStatus {
}

case class EnvStatus (
	cpuUsedPct     : Double,
	memoryUsedPct  : Double,
	swapUsedPct    : Double,
	diskUsedPct    : Double,
	networkUsedPct : Double
)
	val cpuHiHr  : Double
	val cpuLowHr : Double
	val cpuAvgHr : Double

	val memHiHr  : Double
	val memLowHr : Double
	val memAvgHr : Double

	val swpHiHr  : Double
	val swpLowHr : Double
	val swpAvgHr : Double

	val dskHiHr  : Double
	val dskLowHr : Double
	val dskAvgHr : Double

	val memoryUsed  : List[Option[Int]]
	val memoryAvail : Int
	val networkUsed : List[Option[Int]]
}

trait EnvStatusMeter {
	val statusCommand : String
	def readMetrics
	def getMetrics() : EnvStatus
}

case class FloatingHistogram[T](
	numEntries : Int
) {
	private val buffer = new scala.collection.mutable.ListBuffer[Option[T]]

	def add( t:Option[T] ) = {
		buffer += t
		if( buffer.size > numEntries ) buffer.trimStart(1)
	}

	def entries = buffer.toList
}

// case class AwsStatus() extends EnvStatus
// case class LocalStatus() extends EnvStatus
// --aws-access-key-id=AKIAI3W5DSLSQ2U6OU5A --aws-secret-key=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
/*
::: NBCU Prod EP1 $ ./mon-get-instance-stats.pl --aws-access-key-id=AKIAI3W5DSLSQ2U6OU5A --aws-secret-key=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

Instance i-bd6d70ec statistics for the last 1 hour.

CPU Utilization
    Average: 10.54%, Minimum: 8.33%, Maximum: 12.67%

Memory Utilization
    Average: 18.86%, Minimum: 16.54%, Maximum: 21.85%

Swap Utilization
    Average: N/A, Minimum: N/A, Maximum: N/A

Disk Space Utilization for /dev/xvda1 mounted on /
    Average: 4.84%, Minimum: 4.81%, Maximum: 4.86%
*/

// OSX
// top -pid 30563 -l2 | grep 30563 | tail -n 1 | awk -F' ' '{print $3}'
// echo "foo" >> foo | ./z.sh foo > tmp.file && mv tmp.file foo

/*

top -pid 30563 -l2 | grep 30563 | tail -n 1 | awk -F' ' '{print $3}' >> foo | ./z.sh foo > tmp.file && mv tmp.file foo

*/