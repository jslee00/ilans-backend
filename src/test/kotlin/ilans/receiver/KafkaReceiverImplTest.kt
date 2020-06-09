package ilans.receiver

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.StdDateFormat
import ilans.`object`.LogData
import ilans.`object`.LogQueue
import ilans.`object`.LogType
import ilans.common.JsonConfigImpl
import ilans.common.JsonConfigImpl.Companion.jsonMapper
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File
import java.time.Duration
import java.util.*

@Testcontainers
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores::class)
class KafkaReceiverImplTest {
    var logger: Logger = LoggerFactory.getLogger(KafkaReceiverImplTest::class.java)
    val jsonConfig = JsonConfigImpl("config.json").getConfigInstance()

    companion object {
        private val instance: KDockerComposeContainer by lazy { defineDockerCompose()}
        class KDockerComposeContainer(file: File) : DockerComposeContainer<KDockerComposeContainer>(file)

        val jsonMappe = com.fasterxml.jackson.databind.ObjectMapper().apply {
            registerKotlinModule()
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            setDateFormat(StdDateFormat())
        }
        private fun defineDockerCompose() =
                KDockerComposeContainer(File("src/test/resources/docker-compose.yml"))
                        .withExposedService("kafka", 9092)
                        .withEnv("COMPOSE_CONVERT_WINDOWS_PATHS", "1")
                        .withLocalCompose(true)

        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {
            instance.start()
        }

        @AfterAll
        @JvmStatic
        internal fun afterAll() {
            instance.stop()
        }
    }

    @Test
    @DisplayName("Kafka 인스턴스 생성 시험오류 확인 중")
    fun popLogDataTest() {
        val props = Properties()
        props["bootstrap.servers"] = "192.168.99.100:9092"
        props["key.serializer"] = StringSerializer::class.java.canonicalName
        props["value.serializer"] = StringSerializer::class.java.canonicalName
        var producer = KafkaProducer<String, String>(props)

        val fakeLogData = LogData( "ICPS",
        "ujkim",
        "ujkim@satreci.com",
        LogType.ERROR,
        "Superman"
        )

        val fakeLogDataJson = jsonMappe.writeValueAsString(fakeLogData)
        val result = producer.send(ProducerRecord("logs", fakeLogDataJson))
        logger.info(result.toString())

        val kafkaInstance = KafkaReceiverImpl(jsonConfig)
        kafkaInstance.popLogData()
        logger.info(LogQueue.queueOfLogData.size.toString())
        val log = LogQueue.queueOfLogData.poll()
        assertEquals("ICPS", log.serviceName, "서비스 이름이 다르네요.")
    }
}