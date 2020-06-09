package ilans.`object`

data class OutlookData(
        val host: String?,
        val port: Int?,
        val userName: String?,
        val passWord: String?,
        val toAddress:String,
        val subject:String,
        val message:String,
        val attachFile: String?
)
