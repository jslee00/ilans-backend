package ilans.handler

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.StdDateFormat
import ilans.`object`.LogData
import ilans.`object`.LogType
import ilans.common.JsonConfigImpl
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.junit.jupiter.Testcontainers
import kotlinx.coroutines.*
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File
import java.util.Queue

@Testcontainers
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores::class)
class EventHandlerImplTest {
    var logger: Logger = LoggerFactory.getLogger(EventHandlerImplTest::class.java)
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
    fun KakfaMechnismTest() = runBlocking<Unit> {
        async { producingforOperationTest() }
        val statsOfLogs = async { consumingforOperationTest() }
        var errorOfLogcount = 0
        var warnOfLogcount = 0
        var normalOfLogcount = 0
        statsOfLogs.await().forEach() {
            if (it.logType == LogType.ERROR) errorOfLogcount++
            if (it.logType == LogType.WARN) warnOfLogcount++
            if (it.logType == LogType.NORMAL) normalOfLogcount++
        }
        // 왜 첫 번째 항목은 7이 아닌 6이 들어가는 것일까?
        assertEquals(6, errorOfLogcount, "오류 카운트는 " + errorOfLogcount + "가 아니예요")
        assertEquals(11, warnOfLogcount, "오류 카운트는 " + warnOfLogcount + "가 아니예요")
        assertEquals(3, normalOfLogcount, "오류 카운트는 " + normalOfLogcount + "가 아니예요")
    }

    suspend fun producingforOperationTest() {
        val kafkaProducerInst = KafkaProducerImpl(jsonConfig)
        val producer = kafkaProducerInst.createProducer()
        val fakeErrorLogData = LogData("ICPS",
                "ujkim",
                "ujkim@satreci.com",
                LogType.ERROR,
                "Failed to Connect Database"
        )
        val fakeWarnLogData = LogData("ICPS",
                "ujkim",
                "ujkim@satreci.com",
                LogType.WARN,
                "Warn to Meaningful Message not view"
        )
        val fakeSuccessLogData = LogData("ICPS",
            "ujkim",
            "ujkim@satreci.com",
            LogType.NORMAL,
            "Mission Success"
        )
        val fakeLogDataMap = mapOf(fakeErrorLogData to 6, fakeWarnLogData to 10, fakeSuccessLogData to 2)
        for ((fakeLog, count) in fakeLogDataMap) {
            println("결과 $fakeLog,$count")
            for(i in 0..count) {
                println("돌리기 $i,$fakeLog")
                kafkaProducerInst.pushLogDataToKafka(producer, fakeLog, "logs")
                delay(1000)
            }
        }
    }

    suspend fun consumingforOperationTest(): Queue<LogData> {
        val kafkaReceiverInst = KafkaReceiverImpl(jsonConfig)
        val receiver = kafkaReceiverInst.initReceiverSubscibe()
        while(true) {
            kafkaReceiverInst.popLogData(receiver)
            delay(1000)
            println(kafkaReceiverInst.logqueue.queueOfLogData.size)
            if(kafkaReceiverInst.logqueue.queueOfLogData.size == 20) break
        }
        return kafkaReceiverInst.logqueue.queueOfLogData
    }
}