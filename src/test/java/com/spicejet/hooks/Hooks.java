package com.spicejet.hooks;

import com.spicejet.driver.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class Hooks {

    @Before(order = 0)
    public void beforeScenario() {
        DriverFactory.initDriver();
    }

    @After
    public void afterScenario() {
        DriverFactory.quitDriver();
    }
}

