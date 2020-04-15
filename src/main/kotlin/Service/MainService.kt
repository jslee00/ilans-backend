package Service

import Common.JsonConfigImpl
import Object.*
import Sender.EmailSender
import Sender.OutlookAPIImpl
import java.util.HashMap

class RegularNotifier(jsonConfig: JsonConfigImpl?, logStatusList: ArrayList<LogStatusData>) : OutlookAPIImpl() {
    val jsonConfig = jsonConfig
    val logStatusList = logStatusList

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
                sendLog()
            }
        }
    }
}

class EmergencyNotifier(logStatusList : ArrayList<LogStatusData>) : OutlookAPIImpl() {
    val logStatusList = logStatusList

    fun notifyEmergencyStatus() {
        updateEmergencyCondition()
        Notify()
    }

    fun updateEmergencyCondition() {
        logStatusList.forEach {
            it.emergencyNotiFlag = it.queueOfNormals.isEmpty() && it.queueOfWarns.isEmpty() && it.queueOfErrors.isEmpty()
        }
    }

    fun Notify() {
        logStatusList.forEach {
            if(it.emergencyNotiFlag) {
                sendLog()
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
            val regularNf = RegularNotifier(jsonConfig, logStatusList)
            val emergencyNf = EmergencyNotifier(logStatusList)
            regularNf.notifyRegularStatus()
            emergencyNf.notifyEmergencyStatus()
            logStatusList.clear()
            sleep(1000L)
        }
    }
}