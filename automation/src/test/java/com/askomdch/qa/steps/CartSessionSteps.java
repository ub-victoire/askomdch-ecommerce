package com.askomdch.qa.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.askomdch.qa.config.TestConfig;
import com.askomdch.qa.hooks.BrowserHooks;
import com.askomdch.qa.pages.CartPage;
import com.askomdch.qa.pages.CartPage.CartItem;
import com.askomdch.qa.pages.ProductPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.math.BigDecimal;

public class CartSessionSteps {
    private final BrowserHooks browser;
    private String productName;
    private CartItem beforeRefresh;
    private int updatedQuantity;

    public CartSessionSteps(BrowserHooks browser) {
        this.browser = browser;
    }

    @When("the customer adds the configured product to the cart")
    public void addProduct() {
        browser.driver().get(TestConfig.cartProductUrl().toString());
        ProductPage product = new ProductPage(browser.driver());
        productName = product.title();
        product.addToCart();
        browser.driver().get(TestConfig.baseUrl().resolve("/cart/").toString());
    }

    @Then("the cart contains one unit of the selected product")
    public void oneProduct() {
        if (productName == null) {
            throw new IllegalStateException("No product was selected.");
        }
        beforeRefresh = new CartPage(browser.driver()).onlyItem();
        assertEquals(productName, beforeRefresh.name());
        assertEquals(1, beforeRefresh.quantity());
        assertEquals(0, beforeRefresh.unitPrice().compareTo(beforeRefresh.subtotal()));
    }

    @When("the customer updates its cart quantity to {int}")
    public void updateQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must not be negative.");
        }
        oneProduct();
        updatedQuantity = quantity;
        new CartPage(browser.driver()).updateQuantity(quantity);
    }

    @Then("the cart reflects the updated quantity and subtotals")
    public void updatedTotals() {
        CartPage cart = new CartPage(browser.driver());
        CartItem item = cart.onlyItem();
        assertEquals(productName, item.name());
        assertEquals(updatedQuantity, item.quantity());
        assertEquals("Line subtotal must equal unit price times quantity.", 0,
                item.unitPrice().multiply(BigDecimal.valueOf(updatedQuantity))
                .compareTo(item.subtotal()));
        assertEquals("Cart subtotal must equal the only line subtotal.", 0,
                item.subtotal().compareTo(cart.subtotal()));
    }

    @When("the customer removes the product from the cart")
    public void removeProduct() {
        oneProduct();
        new CartPage(browser.driver()).removeItem();
    }

    @Then("the cart is empty after removing the product")
    public void emptyCart() {
        CartPage cart = new CartPage(browser.driver());
        assertEquals("Your cart is currently empty.", cart.emptyNotice());
        assertTrue("Empty cart must not show cart contents.", cart.cartContentsAreHidden());
    }

    @When("the customer refreshes the cart page")
    public void refresh() {
        browser.driver().navigate().refresh();
    }

    @Then("the cart still contains the same product, quantity and price")
    public void cartPersists() {
        if (beforeRefresh == null) {
            throw new IllegalStateException("No cart contents were recorded before refresh.");
        }
        assertEquals(beforeRefresh, new CartPage(browser.driver()).onlyItem());
    }
}
