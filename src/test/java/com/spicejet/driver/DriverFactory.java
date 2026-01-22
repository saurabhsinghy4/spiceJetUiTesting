package com.spicejet.driver;

import com.spicejet.config.Config;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

public final class DriverFactory {
    private static final ThreadLocal<WebDriver> TL_DRIVER = new ThreadLocal<>();

    private DriverFactory() {}

    public static WebDriver getDriver() {
        WebDriver d = TL_DRIVER.get();
        if (d == null) {
            throw new IllegalStateException("WebDriver is not initialized. Did you forget @Before hook?");
        }
        return d;
    }

    public static void initDriver() {
        if (TL_DRIVER.get() != null) return;

        String browser = Config.get("browser");
        if (browser == null) browser = "chrome";

        if (!browser.equalsIgnoreCase("chrome")) {
            throw new IllegalArgumentException("Only chrome is configured right now. browser=" + browser);
        }

        boolean headless = Config.getBoolean("headless", false);

        // Recommended approach: auto-manage the matching chromedriver binary
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1440,900");
        if (headless) {
            options.addArguments("--headless=new");
        }

        WebDriver driver = new ChromeDriver(options);

        int implicitWaitSeconds = Config.getInt("implicitWaitSeconds", 0);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWaitSeconds));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

        TL_DRIVER.set(driver);
    }

    public static void quitDriver() {
        WebDriver d = TL_DRIVER.get();
        if (d != null) {
            try {
                d.quit();
            } finally {
                TL_DRIVER.remove();
            }
        }
    }
}

