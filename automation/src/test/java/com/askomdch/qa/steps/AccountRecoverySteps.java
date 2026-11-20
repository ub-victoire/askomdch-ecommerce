package com.askomdch.qa.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.askomdch.qa.config.RecoveryFixtures;
import com.askomdch.qa.config.TestConfig;
import com.askomdch.qa.config.TestMailboxClient;
import com.askomdch.qa.hooks.BrowserHooks;
import com.askomdch.qa.pages.AccountPage;
import com.askomdch.qa.pages.RecoveryPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class AccountRecoverySteps {
    private final BrowserHooks browser;
    private final RecoveryScenarioContext context;
    private TestMailboxClient mailbox;

    public AccountRecoverySteps(BrowserHooks browser, RecoveryScenarioContext context) {
        this.browser = browser;
        this.context = context;
    }

    @Given("an authorized non-production environment and a controlled synthetic test mailbox")
    public void authorizedEnvironmentAndMailbox() {
        TestConfig.requireAccountRecoveryAllowed();
        TestConfig.baseUrl();
        mailbox = new TestMailboxClient();
    }

    @Given("the customer opens the password recovery form")
    public void openRecoveryForm() {
        browser.driver().get(TestConfig.baseUrl().resolve("/account/lost-password/").toString());
        TestConfig.requireSameOrigin(browser.driver().getCurrentUrl());
    }

    @Given("a registered synthetic test account with access to its mailbox")
    public void registeredAccount() {
        context.username = RecoveryFixtures.username();
        context.email = RecoveryFixtures.email();
        context.recipient = context.email;
        RecoveryFixtures.oldPassword();
        RecoveryFixtures.newPassword();
    }

    @When("the customer requests a password reset using the registered username")
    public void requestWithUsername() {
        request(context.username, context.email);
    }

    @When("the customer requests a password reset using the registered email")
    public void requestWithEmail() {
        request(context.email, context.email);
    }

    @Then("the recovery notice is {string}")
    public void recoveryNotice(String expected) {
        assertEquals(expected, recoveryPage().recoveryMessage());
    }

    @Then("a password reset link is sent to the account mailbox")
    public void resetEmailReceived() {
        context.resetUrl = mailbox().awaitResetLink(context.email, context.requestStarted,
                Duration.ofSeconds(30));
    }

    @Given("a password reset link has been requested for that account")
    public void requestLinkForAccount() {
        request(context.username, context.email);
        assertEquals("Password reset email has been sent.", recoveryPage().recoveryMessage());
        context.resetUrl = mailbox().awaitResetLink(context.email, context.requestStarted,
                Duration.ofSeconds(30));
    }

    @When("the customer uses the emailed link to set a new approved test password")
    public void setNewPasswordFromLink() {
        if (context.resetUrl == null) {
            throw new IllegalStateException("The controlled mailbox has not returned a reset link.");
        }
        TestConfig.requireSameOrigin(context.resetUrl.toString());
        browser.driver().get(context.resetUrl.toString());
        recoveryPage().setNewPassword(RecoveryFixtures.newPassword());
    }

    @Then("the customer can log in with the new password")
    public void loginWithNewPassword() {
        browser.driver().get(TestConfig.baseUrl().resolve("/account/").toString());
        accountPage().enterLogin(context.username, RecoveryFixtures.newPassword(), false);
        accountPage().submitLogin();
        accountPage().waitForDashboard(context.username);
    }

    @Then("the customer cannot log in with the old password")
    public void oldPasswordRejected() {
        accountPage().logout();
        accountPage().enterLogin(context.username, RecoveryFixtures.oldPassword(), false);
        accountPage().submitLogin();
        assertEquals("/account", URI.create(browser.driver().getCurrentUrl()).getPath().replaceAll("/+$", ""));
        assertTrue("The login error for the old password must be displayed.", accountPage().loginErrorVisible());
    }

    @When("the customer requests a password reset with an empty field")
    public void requestWithEmptyField() {
        context.recipient = RecoveryFixtures.email();
        request("", context.recipient);
    }

    @Given("a confirmed unregistered synthetic username")
    public void unregisteredUsername() {
        context.username = "qa-unregistered-" + UUID.randomUUID().toString().substring(0, 8);
        context.recipient = context.username;
    }

    @When("the customer requests a password reset using that username")
    public void requestUnregisteredUsername() {
        request(context.username, context.recipient);
    }

    @Given("an invalid synthetic email address")
    public void invalidEmail() {
        context.recipient = "invalid-email-" + UUID.randomUUID().toString().substring(0, 8) + "@";
        context.email = context.recipient;
    }

    @When("the customer requests a password reset using that email")
    public void requestInvalidEmail() {
        request(context.email, context.recipient);
    }

    @Then("the customer remains on the recovery page")
    public void remainsOnRecoveryPage() {
        TestConfig.requireSameOrigin(browser.driver().getCurrentUrl());
        assertEquals("/account/lost-password",
                URI.create(browser.driver().getCurrentUrl()).getPath().replaceAll("/+$", ""));
    }

    @Then("the recovery error is {string}")
    public void recoveryError(String expected) {
        assertEquals(expected, recoveryPage().recoveryMessage());
    }

    @Then("no password reset email is observed within the configured mail-delivery window")
    public void noRecoveryEmailObserved() {
        String window = System.getenv("ASKOMDCH_TEST_MAILBOX_NEGATIVE_WINDOW_SECONDS");
        long seconds = window == null || window.isBlank() ? 5 : Long.parseLong(window);
        if (seconds < 1 || seconds > 60) {
            throw new IllegalArgumentException(
                    "ASKOMDCH_TEST_MAILBOX_NEGATIVE_WINDOW_SECONDS must be between 1 and 60.");
        }
        mailbox().assertNoMessageDuring(context.recipient, context.requestStarted, Duration.ofSeconds(seconds));
    }

    private void request(String identifier, String recipient) {
        context.recipient = recipient;
        context.requestStarted = Instant.now();
        recoveryPage().requestPasswordReset(identifier);
    }

    private TestMailboxClient mailbox() {
        if (mailbox == null) {
            mailbox = new TestMailboxClient();
        }
        return mailbox;
    }

    private AccountPage accountPage() {
        return new AccountPage(browser.driver());
    }

    private RecoveryPage recoveryPage() {
        return new RecoveryPage(browser.driver());
    }
}
