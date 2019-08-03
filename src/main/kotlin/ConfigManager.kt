package ilans.configmanager

import com.google.gson.Gson
import java.io.BufferedReader
import java.io.File
import java.nio.file.Paths

class ConfigManager(configName: String) {
    val configJsonPath = Paths.get("").toAbsolutePath().toString() + "\\src\\main\\kotlin\\$configName"
    var kafkaServerIP: String? = ""
    var kafkaServerPort: Int? = 0
    var kafkaGroupId: String? = ""
    var accessTopicList: List<String>? = null
    var errorLimitCount: Int? = 0
    var warnLimitCount: Int? = 0
    var errorCsvFileName: String? = ""
    var warnCsvFileName: String? = ""
    var outlookHost : String? = ""
    var outlookPort : String? = ""
    var outlookUser : String? = ""
    var outlookPass : String? = ""

    private fun readJson() : String {
        val bufferedReader: BufferedReader = File(configJsonPath).bufferedReader()
        return bufferedReader.use { it.readText() }
    }

    fun getInstanceOfConfig() : ConfigManager? {
        return Gson().fromJson(readJson(), ConfigManager::class.java)
    }
}

