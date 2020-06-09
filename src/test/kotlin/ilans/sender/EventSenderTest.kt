package ilans.sender

import ilans.`object`.OutlookData
import org.junit.jupiter.api.*

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores::class)
class EventSenderTest {

    @Test
    @DisplayName("Email 메일 전송 실험")
    fun sendLogTest() {
        val outlookData = OutlookData("mail_server_address", port, "mail_name",
                "", "ujkim@satreci.com", "Test Code",
                "Result", "csv path")

        val emailSender = OutlookAPIImpl();
        emailSender.sendLog(outlookData)
    }
}