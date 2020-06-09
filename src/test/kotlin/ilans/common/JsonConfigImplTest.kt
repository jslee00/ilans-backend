package ilans.common

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores

@DisplayNameGeneration(ReplaceUnderscores::class)
class JsonConfigImplTest {
    @Test
    @DisplayName("Json 읽기 시험")
    fun readFileTest() {
        val jsonConfig = JsonConfigImpl("config.json")
        println(jsonConfig.readFile())
    }

    @Test
    @DisplayName("Json 객체 반환 시험")
    fun getConfigInstanceTest() {
        val jsonConfig = JsonConfigImpl("config.json").getConfigInstance()
        assertEquals("172.30.1.14", jsonConfig?.kafkaIP, "Kafka IP가 예상과 달리" + jsonConfig?.kafkaIP + " 이군요")
        assertEquals(9092, jsonConfig?.kafkaPort, "Kafka Port가 예상과 달리" + jsonConfig?.kafkaPort + " 이군요")
        assertEquals("log-processor", jsonConfig?.kafkaGroupId, "Kafka Group ID가 예상과 달리" + jsonConfig?.kafkaGroupId + "이군요")
        assertEquals("logs", jsonConfig?.topicList?.get(0), "Kafka Topic이 예상과 달리" + jsonConfig?.topicList?.get(0) + "이군요")
    }
}