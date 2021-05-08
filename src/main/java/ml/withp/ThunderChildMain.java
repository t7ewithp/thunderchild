package ml.withp;

import ml.withp.gui.STMGeneratorPanel;
import ml.withp.gui.ThunderChildMenuBar;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.swing.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ThunderChildMain {
    public static WebDriver wDriver;

    private static JFrame buildFrame() {

        JFrame frame = new JFrame();
        JPanel pane = new STMGeneratorPanel();
        frame.setContentPane(pane);
        frame.setTitle("Thunder Child");
        JMenuBar bar = new ThunderChildMenuBar(frame);
        frame.setJMenuBar(bar);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        return frame;
    }

    private static void setupDriver() {
        Path driverPath = null;
        try {
            URL driverURL = ThunderChildMain.class.getResource("/chromedriver.exe");
            assert driverURL != null;
            driverPath = Paths.get((driverURL.toURI()));
        } catch(Exception e ) {
            e.printStackTrace();
        }
        assert driverPath != null;
        System.setProperty("webdriver.chrome.driver", driverPath.toAbsolutePath().toString());
        ChromeOptions opt = new ChromeOptions();
        opt.addArguments("--enable-javascript");
        wDriver = new ChromeDriver(opt);
    }

    public static void main(String[] args) {
        setupDriver();
        JFrame frm = buildFrame();
        frm.setVisible(true);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> wDriver.quit()));
    }

}

