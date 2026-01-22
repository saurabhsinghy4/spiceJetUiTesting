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

    /**
     * Opens a pre-filled search for DEL -> LKO, one-way on 2026-02-14
     * with 2 adults and 1 child, matching the provided URL.
     */
    public void openPrefilledSearchForTwoAdultsOneChild() {
        driver.get("https://www.spicejet.com/search?from=DEL&to=LKO&tripType=1&departure=2026-02-14&adult=2&child=1&srCitizen=0&infant=0&currency=INR&redirectTo=/");
    }

    public String title() {
        return driver.getTitle();
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
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

