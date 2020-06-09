import ilans.common.JsonConfigImpl
import ilans.receiver.KafkaReceiverImpl
import ilans.service.MainService


fun main(args: Array<String>) {
    val jsonConfig = JsonConfigImpl("config.json").getConfigInstance()
    val receiver = KafkaReceiverImpl(jsonConfig)
    receiver.popLogData()

    // TODO EMAIL Sender 구현 + 테스트 필요
    val mainService = MainService(jsonConfig)
    mainService.run()
}