package com.askomdch.qa.pages;

import com.askomdch.qa.config.TestConfig;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CartPage {
    private static final By EMPTY_NOTICE = By.cssSelector(".cart-empty");
    private static final By RETURN_BUTTON = By.cssSelector(".return-to-shop a.wc-backward");
    private static final By CART_CONTENTS =
            By.cssSelector(".woocommerce-cart-form, .shop_table.cart, .coupon, .cart_totals");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public String emptyNotice() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(EMPTY_NOTICE)).getText().trim();
    }

    public String returnButtonText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(RETURN_BUTTON)).getText().trim();
    }

    public boolean cartContentsAreHidden() {
        return driver.findElements(CART_CONTENTS).stream().noneMatch(WebElement::isDisplayed);
    }

    public void returnToShop() {
        wait.until(ExpectedConditions.elementToBeClickable(RETURN_BUTTON)).click();
    }

    public CartItem onlyItem() {
        List<WebElement> rows = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector(".woocommerce-cart-form .cart_item")));
        if (rows.size() != 1) {
            throw new IllegalStateException("Cart scenario requires exactly one product.");
        }
        WebElement row = rows.get(0);
        String name = row.findElement(By.cssSelector(".product-name a")).getText().trim();
        int quantity = Integer.parseInt(row.findElement(By.cssSelector(".product-quantity input.qty"))
                .getDomProperty("value"));
        BigDecimal unit = UsdPrice.parse(row.findElement(By.cssSelector(".product-price .woocommerce-Price-amount")).getText());
        BigDecimal subtotal = UsdPrice.parse(row.findElement(By.cssSelector(".product-subtotal .woocommerce-Price-amount")).getText());
        return new CartItem(name, quantity, unit, subtotal);
    }

    public void updateQuantity(int quantity) {
        TestConfig.requireSameOrigin(driver.getCurrentUrl());
        WebElement form = driver.findElement(By.cssSelector(".woocommerce-cart-form"));
        TestConfig.requireSameOrigin(form.getDomProperty("action"));
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".woocommerce-cart-form .cart_item .product-quantity input.qty")));
        input.clear();
        input.sendKeys(Integer.toString(quantity));
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".woocommerce-cart-form button[name='update_cart']"))).click();
        if (quantity > 0) {
            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.cssSelector(".woocommerce-message"), "Cart updated."));
        }
    }

    public BigDecimal subtotal() {
        return UsdPrice.parse(wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".cart_totals .cart-subtotal .woocommerce-Price-amount"))).getText());
    }

    public void removeItem() {
        TestConfig.requireSameOrigin(driver.getCurrentUrl());
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".woocommerce-cart-form .cart_item .product-remove a.remove")));
        TestConfig.requireSameOrigin(link.getDomProperty("href"));
        link.click();
    }

    public record CartItem(String name, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {
    }
}
