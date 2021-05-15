package ml.withp.utility;

import ml.withp.model.Tweet;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
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

    public static Tweet toTweet(CSVRecord csvRaw) {
        return new Tweet(csvRaw.get(2),csvRaw.get(0),DateUtils.dumpToDate(csvRaw.get(1)));
    }

    public static boolean mergeCSV(File mergeOne, File mergeTwo, String outputPath) {
        List<Tweet> tweets = new ArrayList<>();
        try {
            CSVParser inputOne = CSVFormat.RFC4180.parse(makeUtfISR(mergeOne));
            for (CSVRecord rec : inputOne) {
                Tweet t = toTweet(rec);
                if (!tweets.contains(t)) tweets.add(t);
            }
            inputOne.close();
        } catch (IOException e) {
            System.out.println("ERROR: Couldn't load tweets from first merge file!");
            return false;
        }

        try {
            CSVParser inputTwo = CSVFormat.RFC4180.parse(makeUtfISR(mergeTwo));
            for(CSVRecord rec : inputTwo) {
                Tweet t = toTweet(rec);
                if (!tweets.contains(t)) tweets.add(t);
            }
            inputTwo.close();
        } catch (IOException e) {
            System.out.println("ERROR: Couldn't load tweets from second merge file!");
            return false;
        }

        Convert.dumpCSV(tweets, outputPath);
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

