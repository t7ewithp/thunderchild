package ml.withp.utility;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Loader {
    public static InputStream resourceStream(String filename) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        return classloader.getResourceAsStream(filename);
    }

    public static InputStreamReader makeUtfISR(File f) {
        try {
            return new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8);
        }catch (FileNotFoundException e) {
            return null;
        }
    }

    public static boolean mergeCSV(File mergeOne, File mergeTwo, String outputPath) {
        try (PrintWriter out = new PrintWriter(outputPath)){
            CSVPrinter printer = new CSVPrinter(out, CSVFormat.RFC4180);
            CSVParser inputOne = CSVFormat.RFC4180.parse(makeUtfISR(mergeOne));
            for(CSVRecord rec : inputOne) {
                printer.printRecord(rec);
            }
            inputOne.close();
            CSVParser inputTwo = CSVFormat.RFC4180.parse(makeUtfISR(mergeTwo));
            for(CSVRecord rec : inputTwo) {
                printer.printRecord(rec);
            }
            inputTwo.close();
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static List<Pattern> loadPatterns(String path) {
        List<Pattern> ret = new ArrayList<>();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        Loader.resourceStream(path), StandardCharsets.UTF_8));
        try {
            for (String l; (l = reader.readLine()) != null; ) {
                ret.add(Pattern.compile(l));
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
}

