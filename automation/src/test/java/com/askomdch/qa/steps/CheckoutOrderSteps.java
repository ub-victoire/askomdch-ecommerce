package com.askomdch.qa.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.askomdch.qa.config.CheckoutFixtures;
import com.askomdch.qa.config.TestConfig;
import com.askomdch.qa.hooks.BrowserHooks;
import com.askomdch.qa.pages.CheckoutPage;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.net.URI;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class CheckoutOrderSteps {
    private final BrowserHooks browser;
    private String orderEmail;

    public CheckoutOrderSteps(BrowserHooks browser) {
        this.browser = browser;
    }

    @Given("the approved checkout billing fixture is loaded")
    public void loadBilling() {
        page().fillBilling(CheckoutFixtures.billing());
    }

    @Given("the approved alternative shipping fixture is loaded")
    public void loadShipping() {
        page().fillShipping(CheckoutFixtures.shipping());
    }

    @Given("an approved unique billing email is configured")
    public void uniqueEmailConfigured() {
        orderEmail = CheckoutFixtures.uniqueEmailFixture();
        page().fillField("billing_email", orderEmail);
    }

    @Given("an approved valid account password is configured")
    public void validAccountPasswordConfigured() {
        page().fillAccountPassword(CheckoutFixtures.required("ASKOMDCH_ACCOUNT_PASSWORD_VALID"));
    }

    @Given("an approved account password for {string} is configured")
    public void invalidAccountPasswordConfigured(String validation) {
        page().fillAccountPassword(CheckoutFixtures.invalidAccountPassword(validation));
    }

    @Given("an approved registered billing email is configured")
    public void registeredEmailConfigured() {
        orderEmail = CheckoutFixtures.registeredEmail();
        page().fillField("billing_email", orderEmail);
    }

    @Given("the billing fixture uses the documented maximum lengths:")
    public void billingMaximums(DataTable boundaries) {
        assertEquals(5, boundaries.asLists().size());
        page().fillBilling(CheckoutFixtures.billingAtMaximums());
    }

    @Given("the alternative shipping fixture uses the documented maximum lengths:")
    public void shippingMaximums(DataTable boundaries) {
        assertEquals(5, boundaries.asLists().size());
        page().fillShipping(CheckoutFixtures.shippingAtMaximums());
    }

    @Given("the selected country requires the {string} shipping field")
    public void requiredShippingField(String field) {
        page().shippingFieldId(field);
    }

    @Given("the selected country requires the {string} billing field")
    public void requiredBillingField(String field) {
        page().billingFieldId(field);
    }

    @Given("direct bank transfer is selected by default")
    public void bankTransferDefault() {
        page().selectDefaultBankTransfer();
    }

    @When("the customer submits the order with the alternative shipping address")
    public void submitWithShipping() {
        page().submitOrder();
    }

    @When("the customer submits the order with that alternative shipping fixture")
    public void submitMaximumShipping() {
        page().submitOrder();
    }

    @Then("the order-received shipping address matches the alternative shipping fixture")
    public void shippingAddressMatches() {
        assertTrue(page().orderReceivedShippingAddress().contains(
                CheckoutFixtures.required("ASKOMDCH_SHIPPING_FIRST_NAME")));
        assertTrue(page().orderReceivedShippingAddress().contains(
                CheckoutFixtures.required("ASKOMDCH_SHIPPING_ADDRESS")));
        assertTrue(page().orderReceivedShippingAddress().contains(
                CheckoutFixtures.required("ASKOMDCH_SHIPPING_CITY")));
    }

    @When("the customer submits checkout with the {string} shipping field empty")
    public void submitMissingShipping(String field) {
        page().clearField(page().shippingFieldId(field));
        page().submitOrder();
    }

    @When("the customer submits checkout with the {string} billing field empty")
    public void submitMissingBilling(String field) {
        page().clearField(page().billingFieldId(field));
        page().submitOrder();
    }

    @Then("the {string} shipping field is highlighted with a red outline")
    public void shippingFieldHighlighted(String field) {
        assertTrue("Expected invalid shipping field " + field,
                page().fieldInvalidById(page().shippingFieldId(field)));
    }

    @Then("the {string} billing field has a red border")
    public void billingFieldHighlighted(String field) {
        assertTrue("Expected invalid billing field " + field,
                page().fieldInvalidById(page().billingFieldId(field)));
    }

    @Then("a specific shipping field error is displayed")
    public void shippingErrorDisplayed() {
        assertFalse(page().checkoutErrors().isBlank());
    }

    @Then("the AEC-23 required-field error is displayed for billing {string}")
    public void billingRequiredError(String field) {
        assertTrue(page().checkoutErrors().contains("Billing " + field + " is a required field."));
    }

    @When("the customer submits checkout with non-alphabetical characters in shipping {string}")
    public void submitInvalidShippingCharacters(String field) {
        String id = page().shippingFieldId(field);
        page().fillField(id, page().invalidFieldValue(field));
        page().submitOrder();
    }

    @Then("the invalid shipping {string} is highlighted with a red outline")
    public void invalidShippingHighlighted(String field) {
        assertTrue(page().fieldInvalidById(page().shippingFieldId(field)));
    }

    @Then("the billing {string} is rejected")
    public void billingValueRejected(String field) {
        assertTrue(page().fieldInvalidById(page().billingFieldId(field))
                || !page().checkoutErrors().isBlank());
        assertCheckoutStayed();
    }

    @When("the customer submits checkout with non-alphabetical characters in billing {string}")
    public void submitInvalidBillingCharacters(String field) {
        String id = page().billingFieldId(field);
        page().fillField(id, page().invalidFieldValue(field));
        page().submitOrder();
    }

    @When("the customer places the order")
    public void placeOrder() {
        page().submitOrder();
    }

    @When("the customer selects cash on delivery and places the order")
    public void selectCashAndPlaceOrder() {
        page().selectPaymentMethod("cash on delivery");
        page().submitOrder();
    }

    @Then("the order is submitted using direct bank transfer")
    public void orderUsesBankTransfer() {
        assertTrue(page().orderReceived());
        assertTrue(browser.driver().findElement(By.cssSelector(
                ".woocommerce-order-overview__payment-method strong")).getText().toLowerCase()
                .contains("direct bank transfer"));
    }

    @Then("the order is submitted using cash on delivery")
    public void orderUsesCash() {
        assertTrue(page().orderReceived());
        assertTrue(browser.driver().findElement(By.cssSelector(
                ".woocommerce-order-overview__payment-method strong")).getText().toLowerCase()
                .contains("cash on delivery"));
    }

    @When("the customer submits the order with that billing fixture")
    public void submitMaximumBilling() {
        page().submitOrder();
    }

    @Then("the order is submitted with the billing details")
    public void orderWithBilling() {
        assertTrue(page().orderReceived());
    }

    @Then("the order is submitted with the alternative shipping address")
    public void orderWithShipping() {
        assertTrue(page().orderReceived());
    }

    @When("the guest enables account creation and places the order")
    public void createAccountAndPlaceOrder() {
        page().fillAccountPassword(CheckoutFixtures.required("ASKOMDCH_ACCOUNT_PASSWORD_VALID"));
        page().submitOrder();
    }

    @Then("an account is created using the billing email as its login identifier")
    public void accountCreated() {
        assertTrue(page().orderReceived());
        assertEquals(orderEmail, browser.driver().findElement(By.id("billing_email")).getDomProperty("value"));
    }

    @Then("the order is saved under the new account")
    public void orderSavedForAccount() {
        assertTrue(page().orderReceived());
    }

    @Then("the customer is authenticated on the order-received page")
    public void authenticatedOnReceived() {
        assertTrue(page().orderReceived());
        assertFalse(browser.driver().findElements(By.cssSelector(".woocommerce-form-login"))
                .stream().anyMatch(WebElement::isDisplayed));
    }

    @When("the guest enables account creation and submits checkout with an empty account password")
    public void submitEmptyAccountPassword() {
        page().setAccountCreation(true);
        page().clearField("account_password");
        page().submitOrder();
    }

    @When("the guest enables account creation and submits checkout with that password")
    public void submitInvalidAccountPassword() {
        page().submitOrder();
    }

    @When("the guest enables account creation and submits checkout with that email")
    public void submitDuplicateAccountEmail() {
        page().setAccountCreation(true);
        page().submitOrder();
    }

    @Then("the customer remains on checkout and the order is not placed")
    public void orderNotPlaced() {
        assertCheckoutStayed();
    }

    @Then("the checkout error is {string}")
    public void checkoutError(String expected) {
        assertTrue(page().checkoutErrors().contains(expected));
    }

    private void assertCheckoutStayed() {
        URI expected = TestConfig.baseUrl().resolve("/checkout/");
        URI actual = URI.create(browser.driver().getCurrentUrl());
        assertEquals(expected.getScheme(), actual.getScheme());
        assertEquals(expected.getHost(), actual.getHost());
        assertEquals("/checkout", actual.getPath().replaceAll("/+$", ""));
    }

    private CheckoutPage page() {
        return new CheckoutPage(browser.driver());
    }
}
