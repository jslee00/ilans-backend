package ilans.outlookdata

class OutlookData (host:String?,
                   port:Int?,
                   userName:String?,
                   passWord:String?,
                   toAddress:String?,
                   subject:String?,
                   message:String?,
                   attachFile:String?) {
    val host = host
    val port = port
    val userName = userName
    val passWord = passWord
    val toAddress = toAddress
    val subject = subject
    val message = message
    val attachFile = attachFile
}