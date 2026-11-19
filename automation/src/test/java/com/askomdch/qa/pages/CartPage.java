package com.askomdch.qa.pages;

import java.time.Duration;
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
}
