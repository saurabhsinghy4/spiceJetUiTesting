package com.spicejet.pages;

import com.spicejet.config.Config;
import com.spicejet.utils.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

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

    // Passenger selection locators
    private final By passengerSelector = By.xpath(
            "//div[contains(@class, 'passenger') or contains(@data-testid, 'passenger') or contains(text(), 'Passenger') or contains(text(), 'Adult')]"
    );

    private final By passengerDropdown = By.xpath(
            "//div[contains(@class, 'passenger') or contains(@data-testid, 'passenger')]//div[contains(@class, 'dropdown') or contains(@class, 'select')]"
    );

    // Adult increment/decrement buttons
    private final By adultIncrement = By.xpath(
            "//div[contains(@class, 'passenger') or contains(@data-testid, 'passenger')]//div[contains(text(), 'Adult') or contains(text(), 'Adults')]/following-sibling::div//button[contains(@class, 'increment') or contains(@class, 'plus') or text()='+' or @aria-label='Increment']"
    );

    private final By adultDecrement = By.xpath(
            "//div[contains(@class, 'passenger') or contains(@data-testid, 'passenger')]//div[contains(text(), 'Adult') or contains(text(), 'Adults')]/following-sibling::div//button[contains(@class, 'decrement') or contains(@class, 'minus') or text()='-' or @aria-label='Decrement']"
    );

    // Child increment/decrement buttons
    private final By childIncrement = By.xpath(
            "//div[contains(@class, 'passenger') or contains(@data-testid, 'passenger')]//div[contains(text(), 'Child') or contains(text(), 'Children')]/following-sibling::div//button[contains(@class, 'increment') or contains(@class, 'plus') or text()='+' or @aria-label='Increment']"
    );

    private final By childDecrement = By.xpath(
            "//div[contains(@class, 'passenger') or contains(@data-testid, 'passenger')]//div[contains(text(), 'Child') or contains(text(), 'Children')]/following-sibling::div//button[contains(@class, 'decrement') or contains(@class, 'minus') or text()='-' or @aria-label='Decrement']"
    );

    // Done/Apply button to close passenger selection
    private final By doneButton = By.xpath(
            "//button[contains(text(), 'Done') or contains(text(), 'Apply') or contains(@class, 'done') or contains(@class, 'apply')]"
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

    /**
     * Dismisses any overlays (cookies, popups) that might interfere with interactions
     */
    private void dismissOverlays() {
        try {
            driver.findElements(closeButtonsBestEffort).stream().findFirst().ifPresent(el -> {
                try { el.click(); } catch (Exception ignored) {}
            });
            // Small wait for overlay to disappear
            Thread.sleep(500);
        } catch (Exception ignored) {}
    }

    /**
     * Selects the specified number of adults and children via UI interactions
     * @param adults Number of adults to select
     * @param children Number of children to select
     */
    public void selectPassengers(int adults, int children) {
        dismissOverlays();

        // Try multiple locator strategies to find and click passenger selector
        WebElement passengerElement = null;
        try {
            passengerElement = waitVisible(passengerSelector);
        } catch (Exception e) {
            try {
                passengerElement = waitVisible(passengerDropdown);
            } catch (Exception e2) {
                // Try alternative: look for element containing "Passenger" or "Adult" text
                passengerElement = Waits.explicit(driver).until(
                    ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[contains(text(), 'Passenger') or contains(text(), 'Adult') or contains(text(), '1 Adult')]")
                    )
                );
            }
        }

        // Click to open passenger selection dropdown/modal
        passengerElement.click();
        
        // Wait for passenger selection panel to be visible
        try {
            Waits.explicit(driver).until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class, 'passenger') or contains(@data-testid, 'passenger')]//div[contains(text(), 'Adult')]")
                )
            );
        } catch (Exception e) {
            // If that doesn't work, wait a bit for the dropdown to appear
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}
        }

        // Get current adult count and adjust to target
        int currentAdults = getCurrentAdultCount();
        adjustCount(adultIncrement, adultDecrement, currentAdults, adults);

        // Get current child count and adjust to target
        int currentChildren = getCurrentChildCount();
        adjustCount(childIncrement, childDecrement, currentChildren, children);

        // Click Done/Apply button to confirm selection
        try {
            click(doneButton);
        } catch (Exception e) {
            // If Done button not found, try clicking outside or pressing Escape
            // Some implementations close on clicking outside
            try {
                driver.findElement(By.xpath("//body")).click();
            } catch (Exception ignored) {}
        }
    }

    /**
     * Gets the current number of adults selected
     */
    private int getCurrentAdultCount() {
        try {
            WebElement adultElement = driver.findElement(
                By.xpath("//div[contains(@class, 'passenger') or contains(@data-testid, 'passenger')]//div[contains(text(), 'Adult') or contains(text(), 'Adults')]/following-sibling::div//input | //div[contains(@class, 'passenger')]//div[contains(text(), 'Adult')]/following-sibling::div//span[contains(@class, 'value') or contains(@class, 'count')]")
            );
            String text = adultElement.getText();
            if (text.isEmpty()) {
                text = adultElement.getAttribute("value");
            }
            if (text != null && !text.isEmpty()) {
                // Extract number from text like "2 Adults" or just "2"
                String number = text.replaceAll("[^0-9]", "");
                if (!number.isEmpty()) {
                    return Integer.parseInt(number);
                }
            }
        } catch (Exception e) {
            // If we can't find the count, assume 1 (default)
        }
        return 1;
    }

    /**
     * Gets the current number of children selected
     */
    private int getCurrentChildCount() {
        try {
            WebElement childElement = driver.findElement(
                By.xpath("//div[contains(@class, 'passenger') or contains(@data-testid, 'passenger')]//div[contains(text(), 'Child') or contains(text(), 'Children')]/following-sibling::div//input | //div[contains(@class, 'passenger')]//div[contains(text(), 'Child')]/following-sibling::div//span[contains(@class, 'value') or contains(@class, 'count')]")
            );
            String text = childElement.getText();
            if (text.isEmpty()) {
                text = childElement.getAttribute("value");
            }
            if (text != null && !text.isEmpty()) {
                // Extract number from text
                String number = text.replaceAll("[^0-9]", "");
                if (!number.isEmpty()) {
                    return Integer.parseInt(number);
                }
            }
        } catch (Exception e) {
            // If we can't find the count, assume 0 (default)
        }
        return 0;
    }

    /**
     * Adjusts the count by clicking increment/decrement buttons
     */
    private void adjustCount(By incrementLocator, By decrementLocator, int current, int target) {
        int difference = target - current;
        
        if (difference == 0) {
            return; // Already at target
        }

        By buttonToClick = difference > 0 ? incrementLocator : decrementLocator;
        int clicksNeeded = Math.abs(difference);

        for (int i = 0; i < clicksNeeded; i++) {
            try {
                WebElement button = Waits.explicit(driver).until(
                    ExpectedConditions.elementToBeClickable(buttonToClick)
                );
                button.click();
                // Small delay between clicks
                Thread.sleep(300);
            } catch (Exception e) {
                // If button not found or not clickable, try alternative locators
                try {
                    // Try more generic increment/decrement buttons
                    String buttonText = difference > 0 ? "+" : "-";
                    WebElement altButton = driver.findElement(
                        By.xpath("//button[text()='" + buttonText + "' or contains(@class, '" + (difference > 0 ? "increment" : "decrement") + "')]")
                    );
                    altButton.click();
                    Thread.sleep(300);
                } catch (Exception e2) {
                    // If still can't click, break and continue
                    break;
                }
            }
        }
    }
}

