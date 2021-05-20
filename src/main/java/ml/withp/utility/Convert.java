package ml.withp.utility;

import ml.withp.model.Tweet;
import ml.withp.model.YTComment;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

//can't do checked puts into the jsonobject thingies...
@SuppressWarnings("unchecked")
public class Convert {
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public static JSONObject makePad(int len, int order) {
        JSONObject obj = new JSONObject();
        obj.put("partType", "Pixel Gap");
        obj.put("sortOrder", order);
        obj.put("value", len);
        return obj;
    }

    public static JSONObject makeSpeedOverride(String txt, double speed) {
        JSONObject overrides = new JSONObject();
        //this is for later, but it's worthless right now.
        for(String s : new String[]{
                "duration", "intermission", "scrollSpeed", "font",
                "fontSize", "fontColor", "arrival", "departure",
                "alignment"}) {
            overrides.put(s,"");
        }

        for(String s: new String[]{"bold","italic","overstrike"}) overrides.put(s, false);

        return overrides;
    }

    public static void fileCSVToSTM(String path, double speed) throws IOException {
        final File inpFile = new File(path + ".csv");
        final Path outPath = Paths.get(path + ".stm");
        CSVParser inputParser = CSVFormat.RFC4180.parse(Loader.makeUtfISR(inpFile));
        JSONArray core = LstToSTM(loadCSV(inputParser, 0), speed);
        String out = core.toJSONString();
        Files.write(outPath, out.getBytes(CHARSET), StandardOpenOption.CREATE);
    }

    public static List<String> loadCSV(CSVParser input, int targetColumn) {
        List<String> lst = new ArrayList<>();
        for(CSVRecord rec : input) {
            lst.add(rec.get(targetColumn));
        }
        return lst;
    }

    public static void dumpRawCSV(List<String> raws, String filename) {
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8))){
            CSVPrinter printer = new CSVPrinter(out, CSVFormat.RFC4180);
            for(String raw : raws) {
                printer.print(raw);
                printer.println();
            }
            printer.flush();
            printer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void dumpTweetCSV(List<Tweet> tweets, String filename) {
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8))){
            CSVPrinter printer = new CSVPrinter(out, CSVFormat.RFC4180);
            for(Tweet t : tweets) {
                printer.print(t.getData());
                printer.print(t.getDay());
                printer.print(t.getAuthor());
                printer.println();
            }
            printer.flush();
            printer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray LstToSTM(List<String> input, double speed) {
        JSONArray core = new JSONArray();

        for(int i = 0; i < input.size(); i++) {
            String elm = input.get(i);

        JSONObject part = new JSONObject();
        part.put("nickname", Integer.toString(i,Character.MAX_RADIX));
        part.put("sortOrder", Integer.toString(i));

        JSONArray subPart = new JSONArray();
       // if(padding > 0) subPart.add(makePad(padding / 4 + 4, 1));
        JSONObject payload = new JSONObject();

        payload.put("partType", "Text");
        payload.put("sortOrder", 2);
        payload.put("value", elm);
        subPart.add(payload);
        //if(padding > 0) subPart.add(makePad(padding / 4 + 4, 3));

        part.put("parts", subPart);
        part.put("overrides", makeSpeedOverride(elm, speed));
        core.add(part);
    }
    return core;
}

    public static void dumpYTCommentCSV(List<YTComment> comments, String filename) {
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8))){
            CSVPrinter printer = new CSVPrinter(out, CSVFormat.RFC4180);
            for(YTComment y : comments) {
                printer.print(y.toString());
                printer.print(y.getData());
                printer.print(y.getAuthor());
                printer.println();
            }
            printer.flush();
            printer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
