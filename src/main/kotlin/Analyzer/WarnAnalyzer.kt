package ilans.analyzer

import ilans.common.io.IOHandler
import ilans.configmanager.ConfigManager
import ilans.logdata.LogQueue
import ilans.outlookdata.OutlookData
import org.apache.commons.lang3.math.NumberUtils

class WarnAnalyzer: Analyzer {
    val configManager = ConfigManager("config.json").getInstanceOfConfig()
    val ioHandler = IOHandler()

    fun notificateLog() {
        for ((emailAddress, valueOfLogList) in LogQueue.queueOfWarnings.groupBy {it.managerEmail}.entries) {
            if (valueOfLogList.size > configManager?.warnLimitCount!!) {
                val log_stats_map = calculateWithQueueContents(valueOfLogList)
                ioHandler.convertMapToCSV(configManager.warnCsvFileName!!, log_stats_map, valueOfLogList.size)
                // TODO Implement Notification Mail(Outlook?)
                val outlookData = OutlookData(configManager.outlookHost, NumberUtils.toInt(configManager.outlookPort),
                        configManager.outlookUser, configManager.outlookPass,
                        emailAddress, "Warning Notification", "Check the Attachments",
                        configManager.errorCsvFileName)
                OutlookEmailSender.sendEmailWithAttachments(outlookData)
            }
        }
    }
}