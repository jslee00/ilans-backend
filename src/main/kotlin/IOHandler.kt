package ilans.common.io

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.nio.file.Files
import java.nio.file.Paths

class IOHandler {
    fun convertMapToCSV(csv_file_path:String, mapOfStats:HashMap<String, Double>, totalSize:Int) {
        val csvWriter = Files.newBufferedWriter(Paths.get(csv_file_path))
        val csvPrinter = CSVPrinter(csvWriter, CSVFormat.DEFAULT
                .withHeader("Message", "Frequency", "Percentage(%)"))
        for ((message, frequency) in mapOfStats) {
            csvPrinter.printRecord(message, frequency, (frequency/totalSize)*100)
        }
        csvPrinter.flush()
        csvPrinter.close()
    }
}