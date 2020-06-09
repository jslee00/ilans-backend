package ilans.service

import ilans.common.Convertor
import ilans.common.JsonConfigImpl
import ilans.`object`.*
import ilans.sender.EmailSender
import ilans.sender.OutlookAPIImpl
import java.util.HashMap

class RegularNotifier(outlookSender: EmailSender<OutlookData>, jsonConfig: JsonConfigImpl?, logStatusList: ArrayList<LogStatusData>) {
    val jsonConfig = jsonConfig
    val logStatusList = logStatusList
    val outlookSender = outlookSender

    fun calculateStats(targetLogList: List<LogData>): HashMap<String, Double> {
        val hashMapStats: HashMap<String, Double> = HashMap<String, Double>()
        for (logData in targetLogList) {
            if (hashMapStats.keys.contains(logData.logMsg)) {
                var x = hashMapStats[logData.logMsg]
                hashMapStats[logData.logMsg] = 1.0 + x!!
            } else {
                hashMapStats[logData.logMsg] = 1.0
            }
        }
        return hashMapStats
    }

    fun notifyRegularStatus() {
        updateRegularCondition()
        Notify()
    }

    fun updateRegularCondition() {
        logStatusList.forEach {
            it.regularNotiFlag = it.queueOfErrors.size > jsonConfig?.errorLimitCnt!!
        }
    }

    fun Notify() {
        logStatusList.forEach {
            if(it.regularNotiFlag) {
                for ((emailAddress, valueOfLogList) in it.queueOfErrors.groupBy {it.managerEmail}.entries) {
                    val log_stats_map = calculateStats(valueOfLogList)
                    Convertor.convertMapToCSV(jsonConfig?.errorCsvFileName, log_stats_map, valueOfLogList.size)
                    val outlookData = OutlookData(jsonConfig?.mailHost, jsonConfig?.mailPort?.toInt(),
                            jsonConfig?.mailId, jsonConfig?.mailPw, emailAddress, "Error Notification",
                            "Check the SubSystem Log Analysis Result", jsonConfig?.errorCsvFileName)
                    outlookSender.sendLog(outlookData)
                }
            }
        }
    }
}

class EmergencyNotifier(outlookSender: EmailSender<OutlookData>, jsonConfig: JsonConfigImpl?, logStatusList : ArrayList<LogStatusData>) {
    val jsonConfig = jsonConfig
    val logStatusList = logStatusList
    val outlookSender = outlookSender

    fun notifyEmergencyStatus() {
        updateEmergencyCondition()
        Notify()
    }

    fun updateEmergencyCondition() {
        jsonConfig?.emergencyServiceNameList?.forEach {
            val targetServiceName = it
            logStatusList.forEach {
                it.emergencyNotiFlag = targetServiceName != it.serviceName
            }
        }
    }

    fun Notify() {
        logStatusList.forEach {
            if(it.emergencyNotiFlag) {
                // Make Emergency Log Template & Outlook Data Sending
//                outlookSender.sendLog()
            }
        }
    }
}

open class MainService(jsonConfig: JsonConfigImpl?) : Thread() {
    val logStatusList = ArrayList<LogStatusData>()
    val jsonConfig = jsonConfig

    fun initMainServiceObj() {
        initServiceNameObj()
        divideLogByServiceName()
    }

    fun initServiceNameObj() {
        LogQueue.queueOfLogData.forEach {
            logStatusList.add(LogStatusData(it.serviceName))
            logStatusList.distinct()
        }
    }

    fun divideLogByServiceName() {
        LogQueue.queueOfLogData.forEach {
            val logData = it
            logStatusList.forEach {
                if (logData.serviceName == it.serviceName) {
                    when(logData.logType) {
                        LogType.NORMAL -> it.queueOfNormals.add(logData)
                        LogType.WARN -> it.queueOfWarns.add(logData)
                        LogType.ERROR -> it.queueOfErrors.add(logData)
                    }
                }
            }
        }
        LogQueue.queueOfLogData.clear()
    }

    override fun run() {
        while(true) {
            initMainServiceObj()
            val regularNf = RegularNotifier(OutlookAPIImpl(), jsonConfig, logStatusList)
            val emergencyNf = EmergencyNotifier(OutlookAPIImpl(), jsonConfig, logStatusList)
            regularNf.notifyRegularStatus()
            emergencyNf.notifyEmergencyStatus()
            logStatusList.clear()
            sleep(1000L)
        }
    }
}