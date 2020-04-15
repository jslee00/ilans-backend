package Receiver

import Common.JsonConfigImpl
import Object.LogData
import Object.LogQueue
import Object.LogType
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.util.*
import kotlin.math.log

interface EventReceiver<T> {
    fun createReceiver() : T
    fun popLogData()
    fun pushLogData(logdata: LogData?)
}

class KafkaReceiverImpl(jsonConfig: JsonConfigImpl?) : EventReceiver<Consumer<String, String>> {
    val jsonConfig = JsonConfigImpl("config.json").getConfigInstance()

    override fun createReceiver(): Consumer<String, String> {
        val props = Properties()
        props["bootstrap.servers"] = jsonConfig?.kafkaIP + ":" + jsonConfig?.kafkaPort
        props["group.id"] = jsonConfig?.kafkaGroupId
        props["key.deserializer"] = StringDeserializer::class.java
        props["value.deserializer"] = StringDeserializer::class.java
        return KafkaConsumer<String, String>(props)
    }

    override fun popLogData() {
        val receiver = createReceiver()
        receiver.subscribe(jsonConfig?.topicList)
        while(true) {
            val records = receiver.poll(Duration.ofSeconds(1))
            records.iterator().forEach {
                val logStr = it.value()
                val logObject = JsonConfigImpl.jsonMapper.readValue(logStr, LogData::class.java)
                pushLogData(logObject)
            }
        }
    }

    override fun pushLogData(logdata: LogData?) {
        LogQueue.queueOfLogData.add(logdata)
    }
}