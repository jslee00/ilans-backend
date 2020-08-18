package ilans.handler


import ilans.`object`.LogData
import ilans.common.JsonConfigImpl
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

/* Consumer 구동을 위한 Producer (For Test) */
interface EventProducer<T> {
    fun createProducer() : T
    fun pushLogDataToKafka(producer: T, logdata: LogData?, topic: String)
}

class KafkaProducerImpl(jsonConfig: JsonConfigImpl?) : EventProducer<Producer<String, String>> {
    val jsonConfig = jsonConfig

    override fun createProducer(): Producer<String, String> {
        val props = Properties()
        props["bootstrap.servers"] = jsonConfig?.kafkaIP + ":" + jsonConfig?.kafkaPort
        props["key.serializer"] = StringSerializer::class.java.canonicalName
        props["value.serializer"] = StringSerializer::class.java.canonicalName
        return KafkaProducer<String, String>(props)
    }

    override fun pushLogDataToKafka(producer: Producer<String, String>, logdata: LogData?, topic: String) {
        val logObject = JsonConfigImpl.jsonMapper.writeValueAsString(logdata)
//        println(logObject)
        producer.send(ProducerRecord(topic, logObject))
    }
}