package Object

import java.util.*

enum class LogType {
    NORMAL, ERROR, WARN, UNKNOWN
}

data class LogData(val serviceName: String,
                   val managerName: String,
                   val managerEmail: String,
                   val logType: LogType,
                   val logMsg: String)

class LogStatusData(serviceName: String) {
    val serviceName = serviceName
    var queueOfNormals: Queue<LogData> = ArrayDeque<LogData>()
    var queueOfWarns: Queue<LogData> = ArrayDeque<LogData>()
    var queueOfErrors: Queue<LogData> = ArrayDeque<LogData>()
    var emergencyNotiFlag: Boolean = false
    var regularNotiFlag: Boolean = false
}

object LogQueue {
    var queueOfLogData: Queue<LogData> = ArrayDeque<LogData>()
}
