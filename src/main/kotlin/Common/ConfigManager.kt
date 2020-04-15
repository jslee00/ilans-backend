package Common

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.StdDateFormat
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.File
import java.nio.file.Paths

interface ConfigManager {
    fun readFile(): String
    fun getConfigInstance(): ConfigManager?
}

class JsonConfigImpl(configName: String) : ConfigManager {
    val configPath: String = Paths.get("").toAbsolutePath().toString() + "\\src\\main\\kotlin\\$configName"
    var kafkaIP: String? = ""
    var kafkaPort: Int? = 0
    var kafkaGroupId: String? = ""
    var topicList: List<String>? = null
    var errorLimitCnt: Int? = 0

    companion object {
        val jsonMapper = ObjectMapper().apply {
            registerKotlinModule()
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            setDateFormat(StdDateFormat())
        }
    }

    override fun readFile() : String {
        val bufferReader: BufferedReader = File(configPath).bufferedReader()
        return bufferReader.use { it.readText() }
    }

    override fun getConfigInstance(): JsonConfigImpl? {
        return Gson().fromJson(readFile(), JsonConfigImpl::class.java)
    }

    fun getKafkaIP(): Any? { return this.kafkaIP }

}
