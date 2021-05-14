package ml.withp.utility;

import ml.withp.model.Tweet;
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
    private static final int PADDING = 1;
    private static final JSONObject frontPixelGap = new JSONObject();
    private static final JSONObject backPixelGap = new JSONObject();
    private static final JSONObject overrides = new JSONObject();

    static {
        frontPixelGap.put("partType", "Pixel Gap");
        frontPixelGap.put("sortOrder", 1);
        frontPixelGap.put("value", PADDING);
        backPixelGap.put("partType", "Pixel Gap");
        backPixelGap.put("sortOrder", 3);
        backPixelGap.put("value", PADDING);
        for(String s : new String[]{
                "duration", "intermission", "scrollSpeed", "font",
                "fontSize", "fontColor", "arrival", "departure",
                "alignment"}) {
            overrides.put(s,"");
        }
        for(String s: new String[]{"bold","italic","overstrike"}) overrides.put(s, false);
    }

    public static void fileCSVToSTM(String path) throws IOException {
        final File inpFile = new File(path + ".csv");
        final Path outPath = Paths.get(path + ".stm");
        CSVParser inputParser = CSVFormat.RFC4180.parse(Loader.makeUtfISR(inpFile));
        JSONArray core = LstToSTM(loadCSV(inputParser, 0));
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

    public static void dumpCSV(List<Tweet> tweets, String filename) {
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

    public static JSONArray LstToSTM(List<String> input) {

    JSONArray core = new JSONArray();
        for(int i = 0; i < input.size(); i++) {
        JSONObject part = new JSONObject();
        part.put("nickname", Integer.toString(i));
        part.put("sortOrder", Integer.toString(i));

        JSONArray subPart = new JSONArray();
        if(PADDING > 0) subPart.add(frontPixelGap);
        JSONObject payload = new JSONObject();

        payload.put("partType", "Text");
        payload.put("sortOrder", 2);
        payload.put("value", input.get(i));
        subPart.add(payload);
        if(PADDING > 0) subPart.add(backPixelGap);

        part.put("parts", subPart);
        part.put("overrides", overrides);
        core.add(part);
    }
    return core;
}
}
