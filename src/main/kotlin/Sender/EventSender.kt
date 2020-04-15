package Sender

import Common.JsonConfigImpl
import Object.LogData
import Object.LogQueue
import Object.LogType
import Object.OutlookData
import java.util.*
import kotlin.collections.ArrayList

// TODO Interface 정의 + 구체화 필요!
interface EmailSender<T> {
    fun getAuth(emailData:T)
    fun getSession()
    fun setEmailMsg()
    fun setAttachFile()
    fun sendLog()
}

open class OutlookAPIImpl : EmailSender<OutlookData> {
    override fun sendLog() {
        println("sendLog")
    }
}