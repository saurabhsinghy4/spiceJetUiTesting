package com.spicejet.utils;

import com.spicejet.config.Config;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public final class Waits {
    private Waits() {}

    public static WebDriverWait explicit(WebDriver driver) {
        int seconds = Config.getInt("explicitWaitSeconds", 15);
        return new WebDriverWait(driver, Duration.ofSeconds(seconds));
    }
}

