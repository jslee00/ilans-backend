import ilans.analyzer.MainAnalyzer
import ilans.configmanager.ConfigManager

fun main(args: Array<String>) {
    val configInstance = ConfigManager("config.json").getInstanceOfConfig()
    configInstance?.accessTopicList?.forEach { topic -> println(topic)}
    val mainhander = MainEventHandler()
    mainhander.pollingTopics()
    val mainAnalyzer = MainAnalyzer()
    mainAnalyzer.run() // Thread Execution
}