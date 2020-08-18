import ilans.common.JsonConfigImpl


fun main(args: Array<String>) {
    val jsonConfig = JsonConfigImpl("config.json").getConfigInstance()
//    val receiver = KafkaReceiverImpl(jsonConfig)
//    receiver.popLogData()

    // TODO EMAIL Sender 구현 + 테스트 필요
//    val mainService = MainService(jsonConfig)
//    mainService.run()
}