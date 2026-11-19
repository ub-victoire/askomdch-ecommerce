package com.askomdch.qa.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.askomdch.qa.config.TestConfig;
import com.askomdch.qa.config.CheckoutFixtures;
import com.askomdch.qa.hooks.BrowserHooks;
import com.askomdch.qa.pages.CartPage;
import com.askomdch.qa.pages.CartPage.CartItem;
import com.askomdch.qa.pages.CheckoutPage;
import com.askomdch.qa.pages.CheckoutPage.Review;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CheckoutSteps {
    private final BrowserHooks browser;
    private CartItem cartItem;

    public CheckoutSteps(BrowserHooks browser) {
        this.browser = browser;
    }

    @When("the customer opens checkout with that product")
    public void openCheckout() {
        cartItem = new CartPage(browser.driver()).onlyItem();
        browser.driver().get(TestConfig.baseUrl().resolve("/checkout/").toString());
        TestConfig.requireSameOrigin(browser.driver().getCurrentUrl());
        assertEquals("/checkout", URI.create(browser.driver().getCurrentUrl()).getPath().replaceAll("/+$", ""));
    }

    @Then("the returning customer banner is displayed")
    public void returningCustomerBanner() {
        assertEquals("Returning customer? Click here to login", page().loginBanner());
    }

    @When("the customer opens the checkout login form")
    public void openLogin() {
        assertFalse("The checkout login form should start hidden.", page().loginFormVisible());
        page().toggleLogin();
        page().waitForLoginForm(true);
    }

    @Then("the checkout login form is visible")
    public void loginVisible() {
        assertTrue(page().loginFormVisible());
    }

    @When("the customer closes the checkout login form")
    public void closeLogin() {
        page().toggleLogin();
        page().waitForLoginForm(false);
    }

    @Then("the checkout login form is hidden")
    public void loginHidden() {
        assertFalse(page().loginFormVisible());
    }

    @Then("the checkout login password is masked")
    public void maskedPassword() {
        assertEquals("password", page().loginPasswordType());
    }

    @When("the customer submits checkout login without a username or email")
    public void submitWithoutUsername() {
        page().submitWithoutUsername(UUID.randomUUID().toString());
    }

    @Then("the customer remains on the checkout page")
    public void remainsOnCheckout() {
        TestConfig.requireSameOrigin(browser.driver().getCurrentUrl());
        assertEquals("/checkout", URI.create(browser.driver().getCurrentUrl()).getPath().replaceAll("/+$", ""));
    }

    @Then("the checkout login displays {string}")
    public void loginError(String expected) {
        assertEquals(expected, page().loginError());
    }

    @When("the customer logs in with its username and matching password")
    public void loginWithUsername() {
        page().login(CheckoutFixtures.accountUsername(), CheckoutFixtures.accountPassword(), false);
    }

    @When("the customer logs in with its email address and matching password")
    public void loginWithEmail() {
        page().login(CheckoutFixtures.accountEmail(), CheckoutFixtures.accountPassword(), false);
    }

    @When("the customer logs in with Remember me selected")
    public void loginWithRememberMe() {
        page().login(CheckoutFixtures.accountUsername(), CheckoutFixtures.accountPassword(), true);
    }

    @When("the customer logs in with the test account")
    public void loginWithSavedAddressesAccount() {
        loginWithUsername();
    }

    @Then("the customer is logged in on checkout")
    public void loggedInOnCheckout() {
        new WebDriverWait(browser.driver(), Duration.ofSeconds(15)).until(current ->
                current.findElements(org.openqa.selenium.By.cssSelector("form.login"))
                        .stream().noneMatch(org.openqa.selenium.WebElement::isDisplayed));
        TestConfig.requireSameOrigin(browser.driver().getCurrentUrl());
        assertEquals("/checkout", URI.create(browser.driver().getCurrentUrl()).getPath().replaceAll("/+$", ""));
    }

    @io.cucumber.java.en.Given("an approved registered test account is available")
    public void registeredAccountAvailable() {
        CheckoutFixtures.accountUsername();
        CheckoutFixtures.accountEmail();
        CheckoutFixtures.accountPassword();
    }

    @io.cucumber.java.en.Given("an approved registered test account has known saved billing and shipping addresses")
    public void registeredAccountHasAddresses() {
        registeredAccountAvailable();
        CheckoutFixtures.billing();
        CheckoutFixtures.shipping();
    }

    @Then("the returning customer banner is hidden")
    public void returningBannerHidden() {
        assertFalse(browser.driver().findElements(org.openqa.selenium.By.cssSelector(
                ".woocommerce-info a.showlogin")).stream().anyMatch(org.openqa.selenium.WebElement::isDisplayed));
    }

    @Then("the checkout billing and shipping fields contain the account's saved addresses")
    public void savedAddressesDisplayed() {
        CheckoutFixtures.billing().forEach((id, value) -> assertEquals(
                "Saved billing field " + id + " differs.", value,
                browser.driver().findElement(org.openqa.selenium.By.id(id)).getDomProperty("value")));
        CheckoutFixtures.shipping().forEach((id, value) -> assertEquals(
                "Saved shipping field " + id + " differs.", value,
                browser.driver().findElement(org.openqa.selenium.By.id(id)).getDomProperty("value")));
    }

    @When("the customer closes and reopens the browser using the same profile")
    public void reopenBrowser() {
        browser.reopenBrowser();
        browser.driver().get(TestConfig.baseUrl().resolve("/checkout/").toString());
    }

    @Then("the login session is retained")
    public void loginSessionRetained() {
        loggedInOnCheckout();
    }

    @When("the customer submits checkout login with an empty {string} field")
    public void submitEmptyLoginField(String field) {
        if ("username or email".equals(field)) {
            page().submitWithoutUsername(java.util.UUID.randomUUID().toString());
        } else if ("password".equals(field)) {
            page().submitWithoutPassword(CheckoutFixtures.accountUsername());
        } else {
            throw new IllegalArgumentException("Unknown checkout login field: " + field);
        }
    }

    @When("the customer submits its username with an incorrect password")
    public void submitIncorrectPassword() {
        page().login(CheckoutFixtures.accountUsername(), java.util.UUID.randomUUID().toString(), false);
    }

    @When("the customer submits an unregistered {string} through checkout login")
    public void submitUnregisteredLogin(String identifier) {
        String value = "username".equals(identifier) ? "qa-unregistered" : "qa-unregistered@example.invalid";
        page().login(value, java.util.UUID.randomUUID().toString(), false);
    }

    @Then("the AEC-17 incorrect-password error and lost-password link are displayed")
    public void incorrectPasswordErrorAndLink() {
        String expected = "Error: the password you entered for the username "
                + CheckoutFixtures.accountUsername() + " is incorrect. Lost your password?";
        assertEquals(expected, page().loginError());
        assertTrue(page().loginRecoveryLinkVisible());
    }

    @When("the customer opens the checkout lost-password link")
    public void openRecovery() {
        page().openRecovery();
    }

    @Then("the customer is on the account password recovery page")
    public void recoveryPage() {
        URI expected = TestConfig.baseUrl().resolve("/account/lost-password/");
        new WebDriverWait(browser.driver(), Duration.ofSeconds(10))
                .until(current -> {
                    URI actual = URI.create(current.getCurrentUrl());
                    return expected.getScheme().equalsIgnoreCase(actual.getScheme())
                            && expected.getHost().equalsIgnoreCase(actual.getHost())
                            && expected.getPort() == actual.getPort()
                            && "/account/lost-password".equals(actual.getPath().replaceAll("/+$", ""));
                });
    }

    @Then("checkout account creation is unchecked and its password field is hidden")
    public void accountCreationDisabled() {
        assertFalse(page().accountSelected());
        assertFalse(page().accountPasswordVisible());
    }

    @When("the customer enables checkout account creation")
    public void enableAccountCreation() {
        page().setAccountCreation(true);
    }

    @Then("the checkout account password field is visible")
    public void accountPasswordVisible() {
        assertTrue(page().accountPasswordVisible());
    }

    @When("the customer disables checkout account creation")
    public void disableAccountCreation() {
        page().setAccountCreation(false);
    }

    @Then("direct bank transfer is selected with its instructions visible")
    public void bankTransferSelected() {
        assertTrue(page().selectedPaymentAndInstructions("direct bank transfer", "cash on delivery"));
    }

    @Then("cash on delivery is selected with its instructions visible")
    public void cashOnDeliverySelected() {
        assertTrue(page().selectedPaymentAndInstructions("cash on delivery", "direct bank transfer"));
    }

    @When("the customer selects cash on delivery")
    public void selectCash() {
        page().selectPayment("cash on delivery");
    }

    @When("the customer selects direct bank transfer")
    public void selectBank() {
        page().selectPayment("direct bank transfer");
    }

    @Then("the order review displays the selected product quantity and subtotal")
    public void orderReview() {
        if (cartItem == null) {
            throw new IllegalStateException("Cart item was not captured before checkout.");
        }
        Review review = page().orderReview();
        assertEquals("Your order", review.title());
        assertEquals(List.of("Product", "Subtotal"), review.headers());
        assertEquals(cartItem.name() + " × " + cartItem.quantity(), review.product());
        assertEquals(0, cartItem.subtotal().compareTo(review.subtotal()));
    }

    private CheckoutPage page() {
        return new CheckoutPage(browser.driver());
    }
}
