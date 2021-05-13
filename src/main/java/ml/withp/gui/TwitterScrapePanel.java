package ml.withp.gui;

import jdk.nashorn.internal.scripts.JD;
import ml.withp.ThunderChildMain;
import ml.withp.ml.withp.model.Tweet;
import ml.withp.utility.Convert;
import ml.withp.utility.DateUtils;
import ml.withp.utility.TwitterManipulation;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class TwitterScrapePanel extends JPanel {
    private LabeledJDatePicker startDay = new LabeledJDatePicker("Start Day: ");
    private LabeledJDatePicker endDay = new LabeledJDatePicker("End Day: ");
    private LabeledTextField targetUser = new LabeledTextField("Twitter Username Target: ");
    private LabeledTextField targetFilename = new LabeledTextField("Output Filename: ");

    private boolean sanityCheck() {
        if(startDay.getDate().compareTo(endDay.getDate()) >= 0) {
            Helpers.PopupText("Error: Start day must be before end day.");
            return true;
        }

        if(endDay.getDate().compareTo(DateUtils.now()) > 0) {
            Helpers.PopupText("Error: Can't scrape the future, sorry. " + endDay.getDate() + " > " + DateUtils.now());
            return true;
        }

        try {
            File target = new File(targetFilename.getFieldText() + ".csv");

            target.getCanonicalPath();
            if(target.exists()) {
                Helpers.PopupText("Error: " + target + " already exists. Move/Delete it first.");
                return true;
            }
        } catch(Exception e) {
            Helpers.PopupText("Error: " + e.getMessage());
            return true;
        }

        //TODO: check if target user exists???

        return false;
    }

    public TwitterScrapePanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(new JLabel("Twitter Scrape: set the beginning day, the ending day, and the twitter username to target, then hit go!"));
        this.add(new JLabel("WARNING: this uses selinum and requires chrom(ium) installed (it puppets chromium during the scrape)."));
        this.add(new JLabel("I estimate it takes about 15 minutes per month to scrape, due to having to go slow to not freak out twitter."));

        this.add(startDay);
        this.add(endDay);
        this.add(targetUser);
        this.add(targetFilename);
        this.add(new JLabel("When it's done, there'll be a popup. Also progress noise happens in stdout."));
        JButton goButton = new JButton("Begin Scrape");
        goButton.addActionListener(e -> {
            if(!sanityCheck()) {
                List<Tweet> tweets = TwitterManipulation.scrape(targetUser.getFieldText(), startDay.getDate(), endDay.getDate(), ThunderChildMain.wDriver);
                Convert.dumpCSV(tweets, targetFilename.getFieldText() + ".csv");
                Helpers.PopupText("It worked!... Probably?");
            }
        });
        this.add(goButton);
    }
}
