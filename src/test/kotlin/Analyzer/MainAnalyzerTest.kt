package Analyzer

import ilans.analyzer.Analyzer
import ilans.logdata.LogData
import io.kotlintest.shouldBe
import io.kotlintest.specs.AnnotationSpec

class ErrorAnalyzerTest : Analyzer
class MainAnalyzerTest : AnnotationSpec() {
    val mutableList : MutableList<LogData> = ArrayList()
    val referenceHashMap: HashMap<String, Double> = HashMap<String, Double>()
    init {
        val logdata_first = LogData("htlee", "htlee@satreci.com", "ErrorLogType", "I'm genius")
        val logdata_second = LogData("htlee", "htlee@satreci.com", "ErrorLogType", "I'm genius")
        val logdata_third = LogData("htlee", "htlee@satreci.com", "ErrorLogType", "I'm geniun")
        mutableList.add(logdata_first)
        mutableList.add(logdata_second)
        mutableList.add(logdata_third)
        referenceHashMap["I'm genius"] = 2.0
        referenceHashMap["I'm geniun"] = 1.0
    }

    @Test
    fun calculateWithQueuContentsTest() {
        val errorAnalyzer = ErrorAnalyzerTest()
        errorAnalyzer.calculateWithQueueContents(mutableList) shouldBe referenceHashMap
    }



}
