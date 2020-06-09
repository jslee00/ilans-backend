package ilans.common

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.nio.file.Files
import java.nio.file.Paths

object Convertor {
    fun convertMapToCSV(csvFilePath: String?, mapOfStats:HashMap<String, Double>, totalSize:Int) {
        val csvWriter = Files.newBufferedWriter(Paths.get(csvFilePath))
        val csvHdrPrinter = CSVPrinter(csvWriter, CSVFormat.DEFAULT.withHeader("Message", "Frequency", "Percentage(%)"))
        for ((message, frequency) in mapOfStats) {
            csvHdrPrinter.printRecord(message, frequency, (frequency/totalSize) * 100)
        }
        csvHdrPrinter.flush()
        csvHdrPrinter.close()
    }
}