package com.askomdch.qa.pages;

import java.time.Duration;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AccountPage {
    private static final By REGISTRATION_FORM = By.cssSelector("form.woocommerce-form-register");
    private static final By LOGIN_FORM = By.cssSelector("form.woocommerce-form-login");
    private static final Map<String, By> REGISTRATION_CONTROLS = Map.of(
            "username", By.cssSelector("#reg_username"),
            "email", By.cssSelector("#reg_email"),
            "password", By.cssSelector("#reg_password"),
            "register", By.cssSelector("button[name='register']"));

    private final WebDriver driver;

    public AccountPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean registrationControlVisible(String control) {
        By locator = REGISTRATION_CONTROLS.get(control);
        if (locator == null) {
            throw new IllegalArgumentException("Unknown registration control: " + control);
        }
        if ("register".equals(control)) {
            return visibleText(REGISTRATION_FORM, locator, "Register");
        }
        return visibleControl(REGISTRATION_FORM, locator);
    }

    public boolean loginControlsVisible() {
        return visibleControl(LOGIN_FORM, By.cssSelector("#username"))
                && visibleControl(LOGIN_FORM, By.cssSelector("#password"))
                && visibleControl(LOGIN_FORM, By.cssSelector("#rememberme"))
                && visibleText(LOGIN_FORM, By.cssSelector("label.woocommerce-form-login__rememberme"),
                        "Remember me")
                && visibleText(LOGIN_FORM, By.cssSelector("button[name='login']"), "Log in");
    }

    private boolean visibleControl(By formLocator, By controlLocator) {
        return findControl(formLocator, controlLocator, null);
    }

    private boolean visibleText(By formLocator, By controlLocator, String expected) {
        return findControl(formLocator, controlLocator, expected);
    }

    private boolean findControl(By formLocator, By controlLocator, String expectedText) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(current -> {
            for (WebElement form : current.findElements(formLocator)) {
                for (WebElement control : form.findElements(controlLocator)) {
                    if (control.isDisplayed()
                            && (expectedText == null
                            || control.getText().trim().equalsIgnoreCase(expectedText))) {
                        return true;
                    }
                }
            }
            return false;
        });
    }
}
