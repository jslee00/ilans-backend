package ilans.handler

import ilans.common.JsonConfigImpl
import ilans.`object`.LogData
import ilans.`object`.LogQueue
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.util.*

interface EventReceiver<T> {
    fun createReceiver() : T
    fun initReceiverSubscibe() : T
    fun consumingforOperation()
    fun popLogData(receiver: T)
    fun pushLogToQueue(logdata: LogData?)
}

class KafkaReceiverImpl(jsonConfig: JsonConfigImpl?) : EventReceiver<Consumer<String, String>> {
    val jsonConfig = jsonConfig
    val logqueue = LogQueue

    override fun createReceiver(): Consumer<String, String> {
        val props = Properties()
        props["bootstrap.servers"] = jsonConfig?.kafkaIP + ":" + jsonConfig?.kafkaPort
        props["group.id"] = jsonConfig?.kafkaGroupId
        props["key.deserializer"] = StringDeserializer::class.java
        props["value.deserializer"] = StringDeserializer::class.java
        return KafkaConsumer<String, String>(props)
    }

    override fun initReceiverSubscibe() : Consumer<String, String> {
        val receiver = createReceiver()
        receiver.subscribe(jsonConfig?.topicList)
        receiver.seekToBeginning(receiver.assignment())
        return receiver
    }

    override fun popLogData(receiver: Consumer<String, String>) {
        val records = receiver.poll(Duration.ofSeconds(1))
        records.iterator().forEach {
            val logStr = it.value()
            val logObject = JsonConfigImpl.jsonMapper.readValue(logStr, LogData::class.java)
            println(logObject)
            pushLogToQueue(logObject)
        }
    }

    override fun pushLogToQueue(logdata: LogData?) {
        println(logdata?.logType)
        println(logdata?.logMsg)
        logqueue.queueOfLogData.add(logdata)
    }

    override fun consumingforOperation() {
        val receiver = initReceiverSubscibe()
        while(true) {
            popLogData(receiver)
        }
    }
}