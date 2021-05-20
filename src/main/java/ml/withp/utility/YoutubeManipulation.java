package ml.withp.utility;

import ml.withp.model.YTComment;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

//TODO scrape author for comments too and make YTComment objects. This is painful though...
public class YoutubeManipulation {
    private static String formURL(String target) {
        return "https://www.youtube.com/watch?v=" + target;
    }
    public static List<String> scrape(String target, WebDriver driver) {
        String url = formURL(target);
        return lowScrape(url, driver);
    }

    private static void clickAllReplies(WebDriver d) {
        List<WebElement> replyButtons = d.findElements(By.xpath("//*[@id=\"more-replies\"]"));
        for(WebElement raw : replyButtons) {
            try {
                raw.click();
            } catch(Exception e) {
                //don't care :)
            }
        }
    }

    private static List<String> pass(WebDriver d) throws InterruptedException {
        List<String> ret = new ArrayList<>();
        clickAllReplies(d);


        List<WebElement> rawComments = new ArrayList<>();
        try {
            rawComments = d.
                    findElements(By.xpath("//*[@id=\"content-text\"]"));
        } catch(Exception ignored) { }

        for(WebElement raw : rawComments) {
            ret.add(raw.getText());
        }

        ScrapeUtils.scrollOnce(d);
        Thread.sleep(ScrapeUtils.WAIT_TIME / 5);
        ScrapeUtils.scrollOnce(d);
        Thread.sleep(ScrapeUtils.WAIT_TIME / 5);
        return ret;
    }

    private static List<String> lowScrape(String url, WebDriver driver) {
        List<String> comments = new ArrayList<>();

        try {
            driver.get(new URL(url).toString());
            new WebDriverWait(driver, Duration.ZERO.plusMillis(ScrapeUtils.WAIT_TIME * 100)).until(
                    webDriver ->
                            ((JavascriptExecutor) webDriver).executeScript
                                    ("return document.readyState").equals("complete"));

            ScrapeUtils.partialScroll(driver);
            Thread.sleep(ScrapeUtils.WAIT_TIME / 5);

            WebElement comment_section  = driver.findElement(By.xpath("//*[@id=\"comments\"]"));
            ScrapeUtils.scrollIntoView(driver, comment_section);
            Thread.sleep(ScrapeUtils.WAIT_TIME / 5);
            int oldCommentLen;
            do {
                oldCommentLen = comments.size();
                List<String> candidates = pass(driver);
                for(String candidate : candidates) {
                    if (!comments.contains(candidate)) {
                        comments.add(candidate);
                    }
                }
            }while (comments.size() > oldCommentLen);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return comments;
    }

}
