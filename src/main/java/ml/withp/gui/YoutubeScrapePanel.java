package ml.withp.gui;

import ml.withp.ThunderChildMain;
import ml.withp.model.Tweet;
import ml.withp.utility.Convert;
import ml.withp.utility.TwitterManipulation;
import ml.withp.utility.YoutubeManipulation;

import javax.swing.*;
import java.util.List;

public class YoutubeScrapePanel extends JPanel {
    private final LabeledTextField targetVid = new LabeledTextField("Video ID: ");

    private boolean sanityCheck() {
        String txt = targetVid.getFieldText().trim();
        txt = txt.replaceAll("https://www.youtube.com/watch\\?v=","");
        targetVid.setFieldText(txt);

        if(txt.length() != 11) {
            Helpers.PopupText("Error: Pretty sure youtube vid ids are always 11 long.");
            return true;
        }
        return false;
    }

    public YoutubeScrapePanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(new JLabel("Youtube Scrape: set target video id, then hit go!"));
        this.add(targetVid);

        JButton goButton = new JButton("Begin Scrape");
        goButton.addActionListener(e -> {
            if(!sanityCheck()) {
                List<String> comments = YoutubeManipulation.scrape(targetVid.getFieldText(), ThunderChildMain.wDriver);
                Convert.dumpRawCSV(comments, "data/" + targetVid.getFieldText() + ".csv");
                Helpers.PopupText("It worked!... Probably?");
            }
        });
        this.add(goButton);

    }
}
