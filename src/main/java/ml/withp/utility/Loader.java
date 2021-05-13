package ml.withp.utility;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;

public class Loader {
    public static InputStream resourceStream(String filename) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        return classloader.getResourceAsStream(filename);
    }

    public static boolean mergeCSV(File mergeOne, File mergeTwo, String outputPath) {
        try (PrintWriter out = new PrintWriter(outputPath)){
            CSVPrinter printer = new CSVPrinter(out, CSVFormat.RFC4180);
            CSVParser inputOne = CSVFormat.RFC4180.parse(new FileReader(mergeOne));
            for(CSVRecord rec : inputOne) {
                printer.printRecord(rec);
            }
            inputOne.close();
            CSVParser inputTwo = CSVFormat.RFC4180.parse(new FileReader(mergeTwo));
            for(CSVRecord rec : inputTwo) {
                printer.printRecord(rec);
            }
            inputTwo.close();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
