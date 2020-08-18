package ilans.sender

import ilans.`object`.OutlookData
import org.junit.jupiter.api.*

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores::class)
class EventSenderTest {

    @Test
    @DisplayName("Email 메일 전송 실험")
    fun sendLogTest() {
        val outlookData = OutlookData("mail_server_address", port, "mail_name",
                "mail_password", "toAddress", "subject",
                "message", "attachFile")

        val emailSender = OutlookAPIImpl();
        emailSender.sendLog(outlookData)
    }
}