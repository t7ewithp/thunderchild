package ml.withp.utility;

import ml.withp.ml.withp.model.Tweet;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
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
    private static final String SCROLL_SCRIPT = "window.scrollTo(0, document.body.scrollHeight || document.documentElement.scrollHeight)";
    private static final int WAIT_TIME = 15000;

    private static final Pattern SCRAPE_PATTERN = Pattern.compile(
            //I'm all ears for a cleaner regex for this.
            //that said as is it's Author, Day, Payload.
            "</span></div><a href=\"/(.+?)/status/\\d+\" dir=\"auto\" aria-label=\"(.+?)\".+?<span.+?>(.+?)</span></div>");

    private static final List<Pattern> STOP_PATTERNS = new ArrayList<>();
    static {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        Loader.resourceStream("TWITTER_STOPLIST.txt"), StandardCharsets.UTF_8));
        try {
            for (String l; (l = reader.readLine()) != null; ) {
                STOP_PATTERNS.add(Pattern.compile(l));
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static final String[] URL_CONSTANTS =
            {"https://twitter.com/search?f=tweets&vertical=default&q=from%3A",
                    "%20until%3A",
                    "%20since%3A",
                    "&src=typd"};

    private static String formURL(String target, Date targetDay) {
        return URL_CONSTANTS[0] + target +
                URL_CONSTANTS[1] + DateUtils.formatDay(targetDay) +
                URL_CONSTANTS[2] + DateUtils.formatDay(DateUtils.addDays(targetDay, -1)) +
                URL_CONSTANTS[3];
    }

    private static long scrollHeight(JavascriptExecutor ex) {
        Object o = ex.executeScript("return document.documentElement.scrollHeight");
        if(o instanceof Long) return (Long) o;
        return -1; //or throw.
    }

    private static void scrollToBottom(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        long h = -2;//arbitrary number less than -1
        long newH = scrollHeight(js);

        while(h != newH) {
            h = newH;

            //this sucks... (this is also what causes us to sleep and avoid rate limit ban)
            for(int i = 0; i < 10; i++) {
                js.executeScript(SCROLL_SCRIPT);
                try {
                    Thread.sleep(WAIT_TIME / 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            newH = scrollHeight(js);
        }
    }

    public static List<Tweet> scrape(String target, Date startDate, Date endDate, WebDriver driver) {
        List<Tweet> output = new ArrayList<>();
        for(Date currentDate = endDate;
            currentDate.compareTo(startDate) >= 0;
            currentDate = DateUtils.addDays(currentDate, -1)) {

            String url = formURL(target, currentDate);
            String raw = rawScrape(url, driver);
            output.addAll(Objects.requireNonNull(scrapeTweets(target, raw)));
        }
        return output;
    }

    //this will be a url generated by formURL, the end result is a raw page for scrapeTweets...
    private static String rawScrape(String url, WebDriver driver) {
        try {
            driver.get(new URL(url).toString());
            new WebDriverWait(driver, Duration.ZERO.plusMillis(WAIT_TIME)).until(
                    webDriver ->
                            ((JavascriptExecutor) webDriver).executeScript
                                    ("return document.readyState").equals("complete"));
            scrollToBottom(driver);
            return driver.getPageSource();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Tweet> scrapeTweets(String targetUser, String rawPage) {
        if(rawPage == null) return null;
        List<Tweet> tweets = new ArrayList<>();
        Matcher mtchr = SCRAPE_PATTERN.matcher(rawPage);
        while(mtchr.find()) {
            //TODO do this somewhere elsewhere and cleaner
            String cleanedBody = mtchr.group(3).
                    replaceAll("</span>","").
                    replaceAll("</a>","").
                    replaceAll("</div>", "").
                    replaceAll("<div.+?>","").
                    replaceAll("<span.+?>"," ").
                    replaceAll("<a.+?>", "").
                    replaceAll("<g.*?>","").
                    replaceAll("<path.*?>","").
                    replaceAll("</path>","").
                    replaceAll("</g>", "").
                    replaceAll("<img.*?>", "").
                    replaceAll("</svg>","").
                    replaceAll("</img>","").
                    replaceAll("<link.*?>","").
                    replaceAll("</link>","").
                    replaceAll("<video.*?>","").
                    replaceAll("</video>", "").
                    replaceAll("<svg.+?>","");

            Tweet twt = new Tweet(mtchr.group(1), cleanedBody.trim(), DateUtils.twtToDate(mtchr.group(2)));
            if(!twt.getAuthor().equalsIgnoreCase(targetUser) || twt.getData().length() == 0) continue;
            boolean cont = false;
            for(Pattern p : STOP_PATTERNS) {
                if(p.matcher(twt.getData()).matches()) {
                    cont = true;
                    break;
                }
            }
            if(!cont) {
                System.out.println(twt);
                tweets.add(twt);
            }
        }
        return tweets;
    }



}
