package com.askomdch.qa.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.askomdch.qa.config.TestConfig;
import com.askomdch.qa.hooks.BrowserHooks;
import com.askomdch.qa.pages.CatalogPage;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SortingSteps {
    private final BrowserHooks browser;
    private List<BigDecimal> ascendingPrices;

    public SortingSteps(BrowserHooks browser) {
        this.browser = browser;
    }

    @Then("the default sorting option is {string}")
    public void defaultSorting(String expected) {
        assertEquals(expected, new CatalogPage(browser.driver()).selectedSortingOption());
    }

    @Then("the sorting options include:")
    public void sortingOptions(DataTable expected) {
        assertTrue("The specified sorting options must be available.",
                new CatalogPage(browser.driver()).sortingOptions().containsAll(expected.asList()));
    }

    @Given("the customer opens the Men category")
    public void openMenCategory() {
        new CatalogPage(browser.driver()).openMenCategory();
    }

    @When("the customer selects {string}")
    public void selectSorting(String label) {
        new CatalogPage(browser.driver()).selectSorting(label);
    }

    @Then("the Men category remains selected")
    public void categoryRemainsSelected() {
        URI expected = TestConfig.baseUrl().resolve("/product-category/men/");
        URI actual = URI.create(browser.driver().getCurrentUrl());
        assertEquals(expected.getScheme(), actual.getScheme());
        assertEquals(expected.getHost(), actual.getHost());
        assertEquals(expected.getPort(), actual.getPort());
        assertEquals("/product-category/men", actual.getPath().replaceAll("/+$", ""));
    }

    @Then("the visible product prices are in ascending order")
    public void ascendingPrices() {
        ascendingPrices = new CatalogPage(browser.driver()).visiblePrices();
        assertTrue("At least two distinct prices are needed to exercise sorting.",
                ascendingPrices.stream().distinct().count() >= 2);
        assertEquals("Prices must be in ascending order.", ascendingPrices.stream().sorted().toList(), ascendingPrices);
    }

    @Then("the visible product prices are in descending order")
    public void descendingPrices() {
        if (ascendingPrices == null) {
            throw new IllegalStateException("Ascending prices were not recorded.");
        }
        List<BigDecimal> prices = new CatalogPage(browser.driver()).visiblePrices();
        assertEquals("Prices must be in descending order.",
                prices.stream().sorted(Comparator.reverseOrder()).toList(), prices);
        List<BigDecimal> ascendingCopy = new ArrayList<>(ascendingPrices);
        ascendingCopy.sort(Comparator.reverseOrder());
        assertEquals("Both sort orders must show the same prices.", ascendingCopy, prices);
    }
}
