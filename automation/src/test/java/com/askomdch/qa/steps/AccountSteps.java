package com.askomdch.qa.steps;

import static org.junit.Assert.assertTrue;

import com.askomdch.qa.config.TestConfig;
import com.askomdch.qa.hooks.BrowserHooks;
import com.askomdch.qa.pages.AccountPage;
import com.askomdch.qa.pages.RecoveryPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AccountSteps {
    private final BrowserHooks browser;

    public AccountSteps(BrowserHooks browser) {
        this.browser = browser;
    }

    @Given("an authorized test environment is configured")
    public void environmentIsConfigured() {
        TestConfig.baseUrl();
    }

    @When("the customer opens the account page")
    public void openAccountPage() {
        browser.driver().get(TestConfig.baseUrl().resolve("/account/").toString());
    }

    @When("the customer opens the lost-password page")
    public void openLostPasswordPage() {
        browser.driver().get(TestConfig.baseUrl().resolve("/account/lost-password/").toString());
    }

    @Then("the registration form displays the {string} control")
    public void registrationControlIsVisible(String control) {
        assertTrue("Registration control is not visible: " + control,
                new AccountPage(browser.driver()).registrationControlVisible(control));
    }

    @Then("the login form displays its required controls")
    public void loginControlsAreVisible() {
        assertTrue("Login form controls are not all visible.",
                new AccountPage(browser.driver()).loginControlsVisible());
    }

    @Then("the lost-password form displays its instruction and required controls")
    public void recoveryControlsAreVisible() {
        assertTrue("Lost-password instruction or controls are not visible.",
                new RecoveryPage(browser.driver()).instructionAndControlsVisible());
    }
}
