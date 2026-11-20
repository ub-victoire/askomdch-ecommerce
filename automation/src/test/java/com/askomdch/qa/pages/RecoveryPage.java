package com.askomdch.qa.pages;

import com.askomdch.qa.config.TestConfig;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RecoveryPage {
    private final WebDriver driver;

    public RecoveryPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean instructionAndControlsVisible() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(current -> {
            for (WebElement form : current.findElements(By.cssSelector("form.lost_reset_password"))) {
                boolean instruction = form.findElements(By.cssSelector("p")).stream()
                        .anyMatch(p -> p.isDisplayed() && !p.getText().isBlank()
                                && p.findElements(By.cssSelector("input, button, label")).isEmpty());
                boolean input = form.findElements(By.cssSelector("#user_login")).stream()
                        .anyMatch(WebElement::isDisplayed);
                boolean button = form.findElements(By.cssSelector("button[type='submit']")).stream()
                        .anyMatch(element -> element.isDisplayed()
                                && element.getText().trim().equalsIgnoreCase("Reset password"));
                if (instruction && input && button) {
                    return true;
                }
            }
            return false;
        });
    }

    public void requestPasswordReset(String identifier) {
        TestConfig.requireAccountRecoveryAllowed();
        TestConfig.requireSameOrigin(driver.getCurrentUrl());
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("form.lost_reset_password")));
        TestConfig.requireSameOrigin(form.getDomProperty("action"));
        if (!"post".equalsIgnoreCase(form.getDomProperty("method"))) {
            throw new IllegalStateException("Password recovery form must use POST.");
        }
        WebElement field = form.findElement(By.id("user_login"));
        field.sendKeys(Keys.CONTROL, "a", Keys.BACK_SPACE);
        if (!identifier.isEmpty()) {
            field.sendKeys(identifier);
        }
        form.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".woocommerce-error")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".woocommerce-message"))));
    }

    public String recoveryMessage() {
        List<WebElement> errors = driver.findElements(By.cssSelector(".woocommerce-error li"));
        if (!errors.isEmpty() && errors.get(0).isDisplayed()) {
            return errors.get(0).getText().trim().replaceAll("\\s+", " ");
        }
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".woocommerce-message")))
                .getText().trim().replaceAll("\\s+", " ");
    }

    public void setNewPassword(String password) {
        TestConfig.requireAccountRecoveryAllowed();
        TestConfig.requireSameOrigin(driver.getCurrentUrl());
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("form.woocommerce-ResetPassword")));
        TestConfig.requireSameOrigin(form.getDomProperty("action"));
        if (!"post".equalsIgnoreCase(form.getDomProperty("method"))) {
            throw new IllegalStateException("Password reset form must use POST.");
        }
        form.findElement(By.id("password_1")).sendKeys(password);
        form.findElement(By.id("password_2")).sendKeys(password);
        form.findElement(By.cssSelector("button[type='submit']")).click();
    }
}
