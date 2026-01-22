package com.spicejet.steps;

import com.spicejet.pages.SpiceJetHomePage;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class SpiceJetSteps {
    private SpiceJetHomePage homePage;

    @Before(order = 1)
    public void initPages() {
        // Driver is initialized in Hooks.beforeScenario() with order=0
        homePage = new SpiceJetHomePage();
    }

    @Given("I open the SpiceJet home page")
    public void open_home_page() {
        homePage.open();
    }

    @Then("the page title should contain {string}")
    public void title_should_contain(String expected) {
        String actual = homePage.title();
        Assert.assertTrue(
                actual != null && actual.toLowerCase().contains(expected.toLowerCase()),
                "Expected title to contain '" + expected + "' but was '" + actual + "'"
        );
    }

    @Then("the page should show a visible {string} entry point")
    public void should_show_login(String expected) {
        // For now we only validate Login, but keep step readable.
        if (!"Login".equalsIgnoreCase(expected)) {
            throw new IllegalArgumentException("Only 'Login' is implemented right now. Got: " + expected);
        }
        Assert.assertTrue(homePage.isLoginVisible(), "Login entry point was not visible.");
    }

    @When("I select {int} adult(s) and {int} child(ren) as passengers")
    public void select_passengers(int adults, int children) {
        homePage.selectPassengers(adults, children);
    }
}

