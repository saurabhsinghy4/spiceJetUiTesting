package com.spicejet.pages;

import com.spicejet.config.Config;
import org.openqa.selenium.By;

public class SpiceJetHomePage extends BasePage {

    // NOTE: SpiceJet is a dynamic site; keep locators resilient.
    // This tries common "Login" text targets and role-based candidates.
    private final By loginEntryPoint = By.xpath(
            "//*[self::a or self::div or self::span or self::button]" +
            "[normalize-space()='Login' or contains(normalize-space(), 'Login')]" +
            "[not(ancestor-or-self::*[contains(@style,'display: none')])]"
    );

    // Many sites show cookie banner/overlay; we try best-effort closes without failing tests.
    private final By closeButtonsBestEffort = By.xpath(
            "//*[self::button or self::div or self::span]" +
            "[normalize-space()='Accept' or normalize-space()='I Agree' or normalize-space()='Close' or @aria-label='Close' or @aria-label='close']"
    );

    public void open() {
        driver.get(Config.getRequired("baseUrl"));
    }

    public String title() {
        return driver.getTitle();
    }

    public boolean isLoginVisible() {
        // Best-effort attempt to dismiss overlays if present
        try {
            driver.findElements(closeButtonsBestEffort).stream().findFirst().ifPresent(el -> {
                try { el.click(); } catch (Exception ignored) {}
            });
        } catch (Exception ignored) {}

        return isVisible(loginEntryPoint);
    }
}

