package Alarm

import ilans.outlookdata.OutlookData
import java.io.IOException
import java.util.Date
import java.util.Properties

import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

object OutlookEmailSender {
    @Throws(AddressException::class, MessagingException::class)
    fun sendEmailWithAttachments(outlookData : OutlookData) {
        // sets SMTP server properties
        val properties = Properties()
        properties["mail.smtp.host"] = outlookData.host
        properties["mail.smtp.port"] = outlookData.port
        properties["mail.smtp.auth"] = "true"
        properties["mail.smtp.starttls.enable"] = "true"
        properties["mail.user"] = outlookData.userName
        properties["mail.password"] = outlookData.passWord

        // creates a new session with an authenticator
        val auth = object : Authenticator() {
            public override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(outlookData.userName, outlookData.passWord)
            }
        }

        val session = Session.getInstance(properties, auth)
        // creates a new e-mail message
        val msg = MimeMessage(session)
        msg.setFrom(InternetAddress(outlookData.userName))
        val toAddresses = arrayOf(InternetAddress(outlookData.toAddress))
        msg.setRecipients(Message.RecipientType.TO, toAddresses)
        msg.subject = outlookData.subject
        msg.sentDate = Date()

        // creates message part
        val messageBodyPart = MimeBodyPart()
        messageBodyPart.setContent(outlookData.message, "text/html")

        // creates multi-part
        val multipart = MimeMultipart()
        multipart.addBodyPart(messageBodyPart)

        // adds attachments
        if (outlookData.attachFile != null) {
            val attachPart = MimeBodyPart()
            try {
                attachPart.attachFile(outlookData.attachFile)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            multipart.addBodyPart(attachPart)
        }
        // sets the multi-part as e-mail's content
        msg.setContent(multipart)
        // sends the e-mail
        Transport.send(msg)
    }
}