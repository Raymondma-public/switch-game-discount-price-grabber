package managers;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Paths;
import java.time.Duration;

public class BrowserController {

    public static void use(BrowserUseInstance<WebDriver, WebDriverWait> webDriver) throws InterruptedException {

        WebDriver driver = null;
        try {
            System.setProperty("webdriver.chrome.driver",
                    Paths.get("src/main/resources/chromedriver_win32/chromedriver.exe").toString());
            ChromeOptions opt = new ChromeOptions();
            opt.setPageLoadStrategy(PageLoadStrategy.NONE);
            driver = new ChromeDriver(opt);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            System.out.println("before");
            webDriver.accept(driver, wait);

            System.out.println("after");
        } finally {
            if (driver != null) {
                driver.quit();
            }

        }
    }

}
