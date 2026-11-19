package com.askomdch.qa.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.askomdch.qa.config.TestConfig;
import com.askomdch.qa.hooks.BrowserHooks;
import com.askomdch.qa.pages.CartPage;
import com.askomdch.qa.pages.CatalogPage;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class StoreSteps {
    private final BrowserHooks browser;
    private CatalogPage.SelectedProduct selected;

    public StoreSteps(BrowserHooks browser) {
        this.browser = browser;
    }

    @Given("the customer is on the store page")
    public void openStore() {
        browser.driver().get(TestConfig.baseUrl().resolve("/store/").toString());
    }

    @When("the customer opens the {string} header category")
    public void openHeaderCategory(String category) {
        new CatalogPage(browser.driver()).openHeaderCategory(category);
    }

    @Then("the category page heading is {string}")
    public void categoryPageHeading(String expected) {
        assertEquals("The category heading must match the selected category.",
                expected, new CatalogPage(browser.driver()).categoryHeading());
    }

    @Given("the customer has a fresh browser session")
    public void freshBrowserSession() {
        assertTrue("Expected a fresh browser session with no cookies.",
                browser.driver().manage().getCookies().isEmpty());
    }

    @When("the customer searches for {string}")
    public void searchFor(String query) {
        new CatalogPage(browser.driver()).search(query);
    }

    @Then("the matching products are exactly:")
    public void matchingProducts(DataTable table) {
        List<String> expected = table.asList().stream().sorted().toList();
        List<String> actual = new CatalogPage(browser.driver()).productNames().stream().sorted().toList();
        assertEquals("Search results differ from the AEC-15 acceptance criteria.", expected, actual);
    }

    @When("the customer opens the first catalogue product")
    public void openFirstProduct() {
        selected = new CatalogPage(browser.driver()).openFirstProduct();
    }

    @Then("the selected product details are displayed")
    public void selectedProductDetailsAreDisplayed() {
        if (selected == null) {
            throw new IllegalStateException("No catalogue product was selected.");
        }
        WebDriverWait wait = new WebDriverWait(browser.driver(), Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlToBe(selected.url()));
        String actualTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("h1.product_title"))).getText().trim();
        assertEquals("The product detail title must match the selected product.",
                selected.title(), actualTitle);
    }

    @When("the customer opens the cart")
    public void openCart() {
        browser.driver().get(TestConfig.baseUrl().resolve("/cart/").toString());
    }

    @Then("the empty cart notice and return to shop button are displayed")
    public void emptyCartIsDisplayed() {
        CartPage cart = new CartPage(browser.driver());
        assertEquals("Your cart is currently empty.", cart.emptyNotice());
        assertEquals("RETURN TO SHOP", cart.returnButtonText().toUpperCase(java.util.Locale.ROOT));
    }

    @Then("the cart table, coupon section and cart totals are hidden")
    public void cartContentsAreHidden() {
        assertTrue("Empty cart must not show cart contents.", new CartPage(browser.driver()).cartContentsAreHidden());
    }

    @When("the customer returns to the shop")
    public void returnToShop() {
        new CartPage(browser.driver()).returnToShop();
    }

    @Then("the customer arrives at the store page")
    public void customerIsOnStorePage() {
        URI expected = TestConfig.baseUrl().resolve("/store/");
        new WebDriverWait(browser.driver(), Duration.ofSeconds(10))
                .until(driver -> URI.create(driver.getCurrentUrl()).getPath().replaceAll("/+$", "")
                        .equals("/store"));
        URI actual = URI.create(browser.driver().getCurrentUrl());
        assertEquals(expected.getScheme(), actual.getScheme());
        assertEquals(expected.getHost(), actual.getHost());
        assertEquals(expected.getPort(), actual.getPort());
    }
}
