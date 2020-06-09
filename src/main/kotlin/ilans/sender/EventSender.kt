package ilans.sender

import ilans.`object`.OutlookData
import java.io.IOException
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

interface EmailSender<T> {
    fun sendLog(emailData: T)
}

class OutlookAPIImpl() : EmailSender<OutlookData> {
    private fun getAuth(emailData: OutlookData): Authenticator {
        val auth = object : Authenticator() {
            public override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(emailData.userName, emailData.passWord)
            }
        }
        return auth
    }

    private fun getSession(auth: Authenticator, emailData: OutlookData): Session? {
        val properties = Properties()
        properties["mail.smtp.host"] = emailData.host
        properties["mail.smtp.port"] = emailData.port
        properties["mail.smtp.auth"] = "true"
        properties["mail.smtp.starttls.enable"] = "true"
        properties["mail.user"] = emailData.userName
        properties["mail.password"] = emailData.passWord
        return Session.getInstance(properties, auth)
    }

    private fun getEmailInfo(session: Session?, emailData: OutlookData) : MimeMessage {
        val msg = MimeMessage(session)
        msg.setFrom(InternetAddress(emailData.userName))
        val toAddresses = arrayOf(InternetAddress(emailData.toAddress))
        msg.setRecipients(Message.RecipientType.TO, toAddresses)
        msg.subject = emailData.subject
        msg.sentDate = Date()
        return msg
    }

    private fun getAttachMultipart(emailData: OutlookData) : MimeMultipart {
        val msgBodyPart = MimeBodyPart()
        msgBodyPart.setContent(emailData.message, "text/html")
        val multipart = MimeMultipart()
        multipart.addBodyPart(msgBodyPart)

        if (emailData.attachFile != null) {
            val attachPart = MimeBodyPart()
            try {
                attachPart.attachFile(emailData.attachFile)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            multipart.addBodyPart(attachPart)
        }
        return multipart
    }

    private fun getEmailMsg(session: Session?, emailData: OutlookData) : MimeMessage {
        var msg = this.getEmailInfo(session, emailData)
        var attachments = this.getAttachMultipart(emailData)
        msg.setContent(attachments)
        return msg
    }

    override fun sendLog(emailData: OutlookData) {
        val auth = this.getAuth(emailData)
        println(auth)
        val session = this.getSession(auth, emailData)
        println(session)
        val integratedEmailMsg = this.getEmailMsg(session, emailData)
        println(integratedEmailMsg)
        Transport.send(integratedEmailMsg)
    }
}