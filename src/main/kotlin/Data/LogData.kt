package ilans.logdata

import java.util.*

class LogData {
    var managerName: String = "" // Manager person's name
    var managerEmail: String = "" // Manager's Email Address, this is made for notification
    var logType: String = "" // Error, Warning, Normal
    var logMessage: String = ""

    constructor(managerName:String, managerEmail:String, logType:String, logMessage:String) {
        this.managerName = managerName
        this.managerEmail = managerEmail
        this.logType = logType
        this.logMessage = logMessage
    }
}

// Singletone of LogQueue (static define)
object LogQueue {
    var queueOfErrors: Queue<LogData> = ArrayDeque<LogData>()
    var queueOfWarnings: Queue<LogData> = ArrayDeque<LogData>()
    var queueOfNormals: Queue<LogData> = ArrayDeque<LogData>()
}