package com.spicejet.pages;

import com.spicejet.driver.DriverFactory;
import com.spicejet.utils.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public abstract class BasePage {
    protected final WebDriver driver;

    protected BasePage() {
        this.driver = DriverFactory.getDriver();
    }

    protected WebElement waitVisible(By locator) {
        return Waits.explicit(driver).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected boolean isVisible(By locator) {
        try {
            return waitVisible(locator).isDisplayed();
        } catch (Exception ignored) {
            return false;
        }
    }

    protected void click(By locator) {
        waitVisible(locator).click();
    }
}

