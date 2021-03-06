package ml.withp.utility;

import ml.withp.model.Tweet;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwitterManipulation {

    private static final Pattern SCRAPE_PATTERN = Pattern.compile(
            //I'm all ears for a cleaner regex for this.
            //that said as is it's Author, Day, Payload.
            "</span></div><a href=\"/(.+?)/status/\\d+\" dir=\"auto\" aria-label=\"(.+?)\".+?<span.+?>(.+?)</span></div></div>");

    private static final List<Pattern> STOP_PATTERNS = Loader.loadPatterns("TWITTER_STOPLIST.txt");
    private static final List<Pattern> FILTER_PATTERNS = Loader.loadPatterns("TWITTER_FILTERLIST.txt");

    private static final String[] URL_CONSTANTS =
            {"https://twitter.com/search?lang=en&q=(from%3A",
                    ")%20until%3A",
                    "%20since%3A",
                    "&src=typed_query"};


    private static String formURL(String target, Date targetDay) {
        return URL_CONSTANTS[0] + target +
                URL_CONSTANTS[1] + DateUtils.formatDay(targetDay) +
                URL_CONSTANTS[2] + DateUtils.formatDay(DateUtils.addDays(targetDay, -1)) +
                URL_CONSTANTS[3];
    }

    public static List<Tweet> scrape(String target, Date startDate, Date endDate, WebDriver driver) {
        List<Tweet> output = new ArrayList<>();
        for(Date currentDate = endDate;
            currentDate.compareTo(startDate) >= 0;
            currentDate = DateUtils.addDays(currentDate, -1)) {

            String url = formURL(target, currentDate);
            List<String> raws = rawScrape(url, driver);
            for(String raw : raws)
                for(Tweet t : scrapeTweets(target, raw)) {
                    if(!output.contains(t)) output.add(t);
                }
        }
        return output;
    }

    //this will be a url generated by formURL, the end result is a raw page for scrapeTweets...
    private static List<String> rawScrape(String url, WebDriver driver) {
        List<String> output = new ArrayList<>();

        try {
            driver.get(new URL(url).toString());
            new WebDriverWait(driver, Duration.ZERO.plusMillis(ScrapeUtils.WAIT_TIME * 100)).until(
                    webDriver ->
                            ((JavascriptExecutor) webDriver).executeScript
                                    ("return document.readyState").equals("complete"));

            output.add(driver.getPageSource());
            final int PASSES = 10;
            for(int i = 0; i < PASSES; i++) {
                ScrapeUtils.scrollOnce(driver);
                Thread.sleep(ScrapeUtils.WAIT_TIME / PASSES);
                output.add(driver.getPageSource());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    private static List<Tweet> scrapeTweets(String targetUser, String rawPage) {
        if(rawPage == null) return null;
        List<Tweet> tweets = new ArrayList<>();
        Matcher mtchr = SCRAPE_PATTERN.matcher(rawPage);
        while(mtchr.find()) {
            //this is functionally part of filterlist
            //but it's safer to do it this way than to just remove all 'amp;'s
            String cleanedBody = mtchr.group(3).replaceAll("&amp;", "&");

            for(Pattern p : FILTER_PATTERNS)
                cleanedBody = p.matcher(cleanedBody).replaceAll("");
            cleanedBody = cleanedBody.trim();

            Date date = DateUtils.wrongDate();
            try {
                date = DateUtils.twtToDate(mtchr.group(2));
            } catch(Exception e) {
                System.out.println("Error: " + mtchr.group(2) + " is not a date. Ignoring...");
            }

            Tweet twt = new Tweet(mtchr.group(1), cleanedBody.trim(), date);
            if(!twt.getAuthor().equalsIgnoreCase(targetUser) || twt.getData().length() == 0) continue;
            boolean cont = false;
            for(Pattern p : STOP_PATTERNS) {
                if(p.matcher(twt.getData()).matches()) {
                    cont = true;
                    System.out.println("STOPLIST SKIP: " + twt);
                    break;
                }
            }
            if(!cont) {
                System.out.println(twt);
                if(!tweets.contains(twt))
                    tweets.add(twt);
            }
        }
        return tweets;
    }



}
