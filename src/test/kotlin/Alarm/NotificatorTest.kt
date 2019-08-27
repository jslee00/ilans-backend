package Alarm

import ilans.configmanager.ConfigManager
import ilans.outlookdata.OutlookData
import io.kotlintest.specs.AnnotationSpec
import org.apache.commons.lang3.math.NumberUtils

class OutlookEmailSenderTest : AnnotationSpec() {
    val configInstance : ConfigManager? = ConfigManager("config.json").getInstanceOfConfig()
    val outlookData = OutlookData(configInstance?.outlookHost,
            NumberUtils.toInt(configInstance?.outlookPort),
            configInstance?.outlookUser,
            configInstance?.outlookPass,
            "ujkim@satreci.com",
            "Error Notification",
            "Check the Attachmnets",
            "D:\\Projects\\Kotlin\\EventNotificationHandler\\src\\main\\kotlin\\test.csv")

    //TODO implement OutlookEmail Reader Function
    //TODO send function must verificate with reader function contents.
    @Test
    fun sendEmailWithAttachmentsTest() {
        OutlookEmailSender.sendEmailWithAttachments(outlookData)
    }
}