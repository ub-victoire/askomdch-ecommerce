package com.askomdch.qa.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.askomdch.qa.config.RegistrationFixtures;
import com.askomdch.qa.config.TestConfig;
import com.askomdch.qa.hooks.BrowserHooks;
import com.askomdch.qa.pages.AccountPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;

public class AccountRegistrationSteps {
    private final BrowserHooks browser;
    private String username;
    private String email;
    private String password;
    private final AccountScenarioContext accountContext;

    public AccountRegistrationSteps(BrowserHooks browser, AccountScenarioContext accountContext) {
        this.browser = browser;
        this.accountContext = accountContext;
    }

    @Given("an authorized non-production environment with disposable synthetic registration fixtures")
    public void registrationFixturesAvailable() {
        TestConfig.requireAccountRegistrationAllowed();
        RegistrationFixtures.usernameTemplate();
        RegistrationFixtures.uniqueEmail();
        RegistrationFixtures.validPassword();
    }

    @Given("the customer opens the registration form")
    public void openRegistration() {
        browser.driver().get(TestConfig.baseUrl().resolve("/account/").toString());
        TestConfig.requireSameOrigin(browser.driver().getCurrentUrl());
        assertTrue("The registration form must be visible.", page().registrationFormVisible());
    }

    @Given("an unused synthetic username with {string}")
    public void unusedUsername(String variation) {
        if ("the minimum of 5 characters".equals(variation)) {
            username = RegistrationFixtures.uniqueUsername(5, false);
        } else if ("the maximum of 10 characters".equals(variation)) {
            username = RegistrationFixtures.uniqueUsername(10, false);
        } else if ("mixed uppercase and lowercase letters".equals(variation)) {
            username = RegistrationFixtures.uniqueUsername(10, true);
        } else {
            throw new IllegalArgumentException("Unknown username variation: " + variation);
        }
    }

    @Given("an unused valid synthetic email and valid password")
    public void unusedEmailAndPassword() {
        email = RegistrationFixtures.uniqueEmail();
        password = RegistrationFixtures.validPassword();
        if (username == null) {
            username = RegistrationFixtures.uniqueUsername(10, true);
        }
    }

    @Given("an unused valid synthetic username and email")
    public void unusedUsernameAndEmail() {
        username = RegistrationFixtures.uniqueUsername(10, true);
        email = RegistrationFixtures.uniqueEmail();
        password = RegistrationFixtures.validPassword();
    }

    @Given("a valid synthetic password")
    public void validPassword() {
        password = RegistrationFixtures.validPassword();
    }

    @Given("an unused valid synthetic username and valid password")
    public void unusedUsernameAndValidPassword() {
        username = RegistrationFixtures.uniqueUsername(10, true);
        password = RegistrationFixtures.validPassword();
    }

    @Given("a synthetic email address without an at sign")
    public void invalidEmail() {
        email = "invalid-email-without-at-sign";
    }

    @Given("an empty registration email")
    public void emptyEmail() {
        email = "";
    }

    @Given("an empty registration password")
    public void emptyPassword() {
        password = "";
    }

    @Given("an existing synthetic account username")
    public void existingUsername() {
        username = RegistrationFixtures.existingUsername();
    }

    @Given("an existing synthetic account email")
    public void existingEmail() {
        email = RegistrationFixtures.existingEmail();
    }

    @Given("all registration fields are empty")
    public void emptyRegistration() {
        username = "";
        email = "";
        password = "";
        page().clearRegistration();
    }

    @Given("unused valid synthetic registration details")
    public void validRegistrationDetails() {
        username = RegistrationFixtures.uniqueUsername(10, true);
        email = RegistrationFixtures.uniqueEmail();
        password = RegistrationFixtures.validPassword();
    }

    @Given("the customer has registered a disposable synthetic account")
    public void registerDisposableAccount() {
        validRegistrationDetails();
        fillAndSubmit();
        accountContext.username(username);
        page().waitForDashboard(username);
    }

    @Given("the customer has logged out")
    public void customerLogsOut() {
        page().logout();
    }

    @When("the customer submits the registration form")
    public void submitRegistration() {
        fillAndSubmit();
        if (page().loginFormVisible()) {
            accountContext.username(username);
        }
    }

    @When("the customer submits the registration form with the existing username")
    public void submitWithExistingUsername() {
        email = RegistrationFixtures.uniqueEmail();
        password = RegistrationFixtures.validPassword();
        fillAndSubmit();
    }

    @When("the customer submits the registration form with the existing email")
    public void submitWithExistingEmail() {
        username = RegistrationFixtures.uniqueUsername(10, true);
        password = RegistrationFixtures.validPassword();
        fillAndSubmit();
    }

    @When("the customer logs in with the new username and password")
    public void loginNewUsername() {
        page().enterLogin(username, password, false);
        page().submitLogin();
    }

    @When("the customer logs out and logs in with the new email and password")
    public void logoutAndLoginEmail() {
        page().logout();
        page().enterLogin(email, password, false);
        page().submitLogin();
    }

    @Then("the account is created")
    @Then("the customer is automatically logged in")
    public void accountCreatedAndLoggedIn() {
        accountContext.username(username);
        page().waitForDashboard(username);
    }

    @Then("the account dashboard displays {string} followed by the username")
    public void dashboardGreeting(String greeting) {
        if (!"Hello".equals(greeting)) {
            throw new IllegalArgumentException("Unexpected account dashboard greeting.");
        }
        page().waitForDashboard(username);
    }

    @Then("the account navigation includes Dashboard, Orders, Downloads, Addresses, Account details, and Logout")
    public void accountNavigation() {
        assertTrue("The account navigation is missing one or more documented links.",
                page().accountNavigationContains(
                        List.of("Dashboard", "Orders", "Downloads", "Addresses", "Account details", "Logout")));
    }

    @Then("the customer remains on the account page")
    public void remainsOnAccount() {
        TestConfig.requireSameOrigin(browser.driver().getCurrentUrl());
        assertEquals("/account", java.net.URI.create(browser.driver().getCurrentUrl())
                .getPath().replaceAll("/+$", ""));
    }

    @Then("registration is rejected")
    public void registrationRejected() {
        remainsOnAccount();
        assertTrue("Registration error must be displayed.", !page().registrationError().isBlank());
    }

    @Then("the registration error is {string}")
    public void registrationError(String expected) {
        assertEquals(expected, page().registrationError());
    }

    @Then("the registration validation message is {string}")
    public void registrationValidationMessage(String expected) {
        assertEquals(expected, page().registrationValidationMessage());
    }

    private void fillAndSubmit() {
        page().enterRegistration(username, email, password);
        page().submitRegistration();
    }

    private AccountPage page() {
        return new AccountPage(browser.driver());
    }
}
