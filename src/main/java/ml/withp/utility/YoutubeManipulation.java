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
    public static List<YTComment> scrape(String target, WebDriver driver) {
        String url = formURL(target);
        return lowScrape(url, driver);
    }

    private static void clickAllExpansionButtons(WebDriver d) {
        List<WebElement> replyButtons = d.findElements(By.xpath("//*[@id=\"more-replies\"]"));

        for(WebElement raw : replyButtons) {
            try {
                raw.click();
                Thread.sleep(20);
            } catch(Exception ignored) {}
        }
        List<WebElement> readMoreButtons = d.findElements(By.xpath("//tp-yt-paper-button[@id=\"more\"]"));
        for(WebElement raw : readMoreButtons) {
            try {
                raw.click();
                Thread.sleep(20);
            } catch(Exception ignored) {}
        }

    }

    private static void clickVideo(WebDriver d) {
        try {
            WebElement video = d.findElement(By.id("id video"));
            video.click();
            Thread.sleep(20);
        } catch(Exception ignored) {}
    }

    private static List<YTComment> getCommentsByXpath(WebDriver d) {
        List<YTComment> ret = new ArrayList<>();

        try {
            List<WebElement> highLevel = d.findElements(By.xpath("//div[@class=\"style-scope ytd-comment-renderer\"]"));
            for(WebElement raw : highLevel) {
                String rawText = raw.getText();
                if(!rawText.contains("REPLY")) continue;

                rawText = rawText.replaceAll("\n.*?REPLY\\Z","");
                String[] parts = rawText.split("\n", 3);
                if(parts.length != 3) continue;
                if(parts[0].matches("\\d+.?")) continue;
                parts[2] = parts[2].replaceFirst("\\s+\\d+\\Z","");
                parts[2] = parts[2].replaceFirst("\\s+Show less\\Z","");
                YTComment comment = new YTComment(parts[0], parts[2]);
                if(!ret.contains(comment)) ret.add(comment);
            }
        } catch(Exception ignored) { }
        return ret;
    }


    private static List<YTComment> pass(WebDriver d) throws InterruptedException {
        clickAllExpansionButtons(d);

        List<YTComment> ret = getCommentsByXpath(d);

        ScrapeUtils.scrollOnce(d);
        Thread.sleep(ScrapeUtils.WAIT_TIME / 7);
        ScrapeUtils.scrollOnce(d);
        Thread.sleep(ScrapeUtils.WAIT_TIME / 7);

        return ret;
    }

    private static List<YTComment> lowScrape(String url, WebDriver driver) {
        List<YTComment> comments = new ArrayList<>();

        try {
            driver.get(new URL(url).toString());
            new WebDriverWait(driver, Duration.ZERO.plusMillis(ScrapeUtils.WAIT_TIME * 100)).until(
                    webDriver ->
                            ((JavascriptExecutor) webDriver).executeScript
                                    ("return document.readyState").equals("complete"));

            clickVideo(driver);
            ScrapeUtils.partialScroll(driver);
            Thread.sleep(ScrapeUtils.WAIT_TIME / 5);
            ScrapeUtils.partialScroll(driver);

            WebElement comment_section  = driver.findElement(By.xpath("//*[@id=\"comments\"]"));
            ScrapeUtils.scrollIntoView(driver, comment_section);

            Thread.sleep(ScrapeUtils.WAIT_TIME / 5);
            int oldCommentLen;
            do {
                oldCommentLen = comments.size();
                List<YTComment> candidates = pass(driver);
                for(YTComment candidate : candidates) {
                    if (!comments.contains(candidate) && candidate.getData().length() > 0) {
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
