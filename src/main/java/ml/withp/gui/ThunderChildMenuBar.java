package ml.withp.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ThunderChildMenuBar extends JMenuBar {
    private final JFrame host;
    public ThunderChildMenuBar(JFrame frame) {
        host = frame;
        JMenu about = new JMenu("About");
        JMenuItem license = new JMenuItem("License");
        JMenuItem attribution = new JMenuItem("Attribution");
        license.addActionListener(e -> Helpers.PopupText(
                "This Software (Thunder Child) is licensed under the terms of the GNU AGPL version 3.\n" +
                        "For the complete text see https://www.gnu.org/licenses/agpl-3.0.en.html or the copy provided with this program."));

        attribution.addActionListener(e -> Helpers.PopupText(
                "Thunder Child was written by withp (t7ewithp on github thewithp on twitter etc)\n\n" +
                        "Thunder Child makes use of:\n" +
                        "The apache commons CSV library (as an intermediary format),\n" +
                        "Selenium (for scraping),\n" +
                        "and the json-simple library (to make .stm files), all under the Apache License V 2.0\n" +
                        "as well as JDatePicker, which is under a 2 clause BSD License.\n" +
                        "Thunder Child is explicitly designed to support Go1den's Streamticker program," +
                        " which has been released under GNU GPL version 3."));

        about.add(license);
        about.add(attribution);
        JMenu modules = new JMenu("Modules");
        JMenuItem csvManip = new JMenuItem("CSV Manipulation");
        csvManip.addActionListener(e -> {
            host.setContentPane(new STMGeneratorPanel());
            host.pack();
        });

        JMenuItem twitterScraper = new JMenuItem("Twitter Scrape");
        twitterScraper.addActionListener(e -> {
            host.setContentPane(new TwitterScrapePanel());
            host.pack();
        });

        JMenuItem youtubeScraper = new JMenuItem("Youtube Scrape");
        youtubeScraper.addActionListener(e -> {
            //todo
        });
        modules.add(csvManip);
        modules.add(twitterScraper);
        modules.add(youtubeScraper);
        this.add(modules);
        this.add(about);
    }
}
