import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.StdDateFormat
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import ilans.configmanager.ConfigManager
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.util.*

import ilans.logdata.*

class MainEventHandler {
    val configManager = ConfigManager("config.json").getInstanceOfConfig()

    private fun createConsumer(): Consumer<String, String> {
        val props = Properties()
        props["bootstrap.servers"] = configManager?.kafkaServerIP + ":" + configManager?.kafkaServerPort
        props["group.id"] = configManager?.kafkaGroupId
        props["key.deserializer"] = StringDeserializer::class.java
        props["value.deserializer"] = StringDeserializer::class.java
        return KafkaConsumer<String, String>(props)
    }

    // Serialize Data to Json
    val jsonMapper = ObjectMapper().apply {
        registerKotlinModule()
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        setDateFormat(StdDateFormat())
    }

    // Kafka Consuming
    // if there is only topic in list, only get log information. (Duration : 1 seconds)
    // need to Kafka test, correct executable code
    fun pollingTopics() {
        val consumer = createConsumer()
        consumer.subscribe(configManager?.accessTopicList)
        while(true) {
            val records = consumer.poll(Duration.ofSeconds(1))
            records.iterator().forEach {
                val logString = it.value()
                val logMessage = jsonMapper.readValue(logString, LogData::class.java)
                divideLogMessage(logMessage)
            }
        }
    }

    private fun divideLogMessage(logdata: LogData?) {
        when (logdata?.logType) {
            "ErrorLogType" -> LogQueue.queueOfErrors.add(logdata)
            "WarningLogType" -> LogQueue.queueOfWarnings.add(logdata)
            "NormalLogType" -> LogQueue.queueOfNormals.add(logdata)
        }
    }
}

