package ilans.analyzer

import ilans.logdata.*
import kotlin.collections.HashMap

interface Analyzer {
    fun calculateWithQueueContents(targetLogList: List<LogData>): HashMap<String, Double> {
        val hashMapStatistics: HashMap<String, Double> = HashMap<String, Double>()
        for (logData in targetLogList) {
            if (hashMapStatistics.keys.contains(logData.logMessage)) {
                var x = hashMapStatistics[logData.logMessage]
                hashMapStatistics[logData.logMessage] = 1.0 + x!!
            } else {
                hashMapStatistics[logData.logMessage] = 1.0
            }
        }
        return hashMapStatistics
    }
}

open class MainAnalyzer() : Thread() {
    val errorAnalyzer = ErrorAnalyzer()
    val warnAnalyzer = WarnAnalyzer()

    // TODO Analysis of result, To Implement of functions
    override fun run() { // Thread Override, Check the queue count info
        while(true) {
            errorAnalyzer.notificateLog()
            warnAnalyzer.notificateLog()
        }
    }
}