package ilans.analyzer

import ilans.common.io.IOHandler
import ilans.configmanager.ConfigManager
import ilans.logdata.LogQueue
import ilans.outlookdata.OutlookData
import org.apache.commons.lang3.math.NumberUtils.toInt

// Error Logging Statistic's calculation
class ErrorAnalyzer: Analyzer {
    val configManager = ConfigManager("config.json").getInstanceOfConfig()
    val ioHandler = IOHandler()

    fun notificateLog() {
        for ((emailAddress, valueOfLogList) in LogQueue.queueOfErrors.groupBy {it.managerEmail}.entries) {
            if (valueOfLogList.size > configManager?.errorLimitCount!!) {
                val log_stats_map = calculateWithQueueContents(valueOfLogList)
                ioHandler.convertMapToCSV(configManager.errorCsvFileName!!, log_stats_map, valueOfLogList.size)
                // TODO Implement Notification Mail(Outlook?)
                val outlookData = OutlookData(configManager.outlookHost, toInt(configManager.outlookPort),
                        configManager.outlookUser, configManager.outlookPass,
                        emailAddress, "Error Notification", "Check the Attachmnets",
                        configManager.errorCsvFileName)
                OutlookEmailSender.sendEmailWithAttachments(outlookData)
            }
            Thread.sleep(100)
        }
    }
}