package ml.withp.utility;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ScrapeUtils {
    private static final String SCROLL_SCRIPT = "window.scrollTo(0, document.body.scrollHeight || document.documentElement.scrollHeight)";
    public static final String PARTIAL_SCROLL = "window.scrollTo(0, document.body.scrollHeight / 2 || document.documentElement.scrollHeight / 2)";
    public static final int WAIT_TIME = 15000;


    public static long scrollHeight(JavascriptExecutor ex) {
        Object o = ex.executeScript("return document.documentElement.scrollHeight");
        if(o instanceof Long) return (Long) o;
        return -1; //or throw.
    }

    public static void scrollIntoView(WebDriver driver, WebElement elm) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", elm);
        new WebDriverWait(driver, Duration.ZERO.plusMillis(ScrapeUtils.WAIT_TIME * 100)).until(
                webDriver ->
                        ((JavascriptExecutor) webDriver).executeScript
                                ("return document.readyState").equals("complete"));
    }

    public static void scrollOnce(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(SCROLL_SCRIPT);
    }

    public static void partialScroll(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(PARTIAL_SCROLL);
    }
}
