package com.askomdch.qa.steps;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.askomdch.qa.config.CheckoutFixtures;
import com.askomdch.qa.config.TestConfig;
import com.askomdch.qa.hooks.BrowserHooks;
import com.askomdch.qa.pages.AccountPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class AccountLoginSteps {
    private final BrowserHooks browser;
    private final AccountScenarioContext accountContext;

    public AccountLoginSteps(BrowserHooks browser, AccountScenarioContext accountContext) {
        this.browser = browser;
        this.accountContext = accountContext;
    }

    @Given("an authorized non-production environment with a registered synthetic test account")
    public void requireAccount() {
        TestConfig.requireAccountLoginAllowed();
        accountContext.username(CheckoutFixtures.accountUsername());
        CheckoutFixtures.accountUsername();
        CheckoutFixtures.accountEmail();
        CheckoutFixtures.accountPassword();
    }

    @Given("the customer opens the account login form")
    public void openLoginForm() {
        browser.driver().get(TestConfig.baseUrl().resolve("/account/").toString());
        TestConfig.requireSameOrigin(browser.driver().getCurrentUrl());
        assertTrue("Account login form must be visible.", page().loginFormVisible());
    }

    @When("the customer logs in using the registered username and password")
    public void loginWithUsername() {
        login(CheckoutFixtures.accountUsername(), false);
    }

    @When("the customer logs in using the registered email and password")
    public void loginWithEmail() {
        login(CheckoutFixtures.accountEmail(), false);
    }

    @When("the customer logs in using a different-case registered username and the matching password")
    public void loginWithDifferentCase() {
        String username = CheckoutFixtures.accountUsername();
        String lower = username.toLowerCase(Locale.ROOT);
        String differentCase = username.equals(lower) ? username.toUpperCase(Locale.ROOT) : lower;
        if (username.equals(differentCase)) {
            throw new IllegalArgumentException("The test username must have letters to test case matching.");
        }
        login(differentCase, false);
    }

    @When("the customer logs in with Remember me checked")
    public void loginRemembered() {
        login(CheckoutFixtures.accountUsername(), true);
        dashboard();
    }

    @When("the customer logs in with Remember me unchecked")
    public void loginNotRemembered() {
        login(CheckoutFixtures.accountUsername(), false);
        dashboard();
    }

    @Given("the customer is logged in to the test account")
    public void loggedIn() {
        loginWithUsername();
        dashboard();
    }

    @When("the customer closes and reopens the browser with the same profile")
    public void reopenBrowser() {
        browser.reopenBrowser();
    }

    @Then("the customer sees the account dashboard")
    public void dashboard() {
        String username = accountContext.username();
        if (username == null) {
            username = CheckoutFixtures.accountUsername();
        }
        page().waitForDashboard(username);
    }

    @Then("the dashboard displays {string} followed by the username")
    public void dashboardGreeting(String greeting) {
        if (!"Hello".equals(greeting)) {
            throw new IllegalArgumentException("Unexpected account dashboard greeting.");
        }
        dashboard();
    }

    @Then("the account login form is displayed")
    public void loginDisplayed() {
        assertTrue("Account login form must be visible.", page().loginFormVisible());
    }

    @Then("the account login form is not displayed")
    public void loginNotDisplayed() {
        assertFalse("Account login form must not be visible.", page().loginFormVisible());
    }

    @When("the customer enters the test account password")
    public void enterPassword() {
        page().enterLogin(CheckoutFixtures.accountUsername(), CheckoutFixtures.accountPassword(), false);
    }

    @Then("the password is masked on screen")
    public void passwordMasked() {
        assertTrue("Account password input must be masked.", page().loginPasswordMasked());
    }

    @When("the customer submits the login form")
    public void submitLogin() {
        page().submitLogin();
    }

    @Then("the password does not appear in the browser URL")
    public void passwordAbsentFromUrl() {
        URI actual = URI.create(browser.driver().getCurrentUrl());
        String decodedUrl = URLDecoder.decode(actual.toString(), StandardCharsets.UTF_8);
        assertFalse("The browser URL must not contain the test password.",
                decodedUrl.contains(CheckoutFixtures.accountPassword()));
        TestConfig.requireSameOrigin(actual.toString());
    }

    private void login(String identifier, boolean remember) {
        page().enterLogin(identifier, CheckoutFixtures.accountPassword(), remember);
        page().submitLogin();
    }

    private AccountPage page() {
        return new AccountPage(browser.driver());
    }
}
