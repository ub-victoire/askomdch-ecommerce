package com.askomdch.qa.pages;

import com.askomdch.qa.config.TestConfig;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ProductPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public ProductPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public String title() {
        String title = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("h1.product_title"))).getText().trim();
        if (title.isBlank()) {
            throw new IllegalStateException("Configured product has no title.");
        }
        return title;
    }

    public void addToCart() {
        TestConfig.requireSameOrigin(driver.getCurrentUrl());
        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("form.cart")));
        TestConfig.requireSameOrigin(form.getDomProperty("action"));
        wait.until(ExpectedConditions.elementToBeClickable(
                form.findElement(By.cssSelector("button[name='add-to-cart']")))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".woocommerce-message")));
    }
}
