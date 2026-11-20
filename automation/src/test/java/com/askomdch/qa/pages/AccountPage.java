package com.askomdch.qa.pages;

import com.askomdch.qa.config.TestConfig;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
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

    public void enterLogin(String username, String password, boolean remember) {
        TestConfig.requireAccountLoginAllowed();
        TestConfig.requireSameOrigin(driver.getCurrentUrl());
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement form = wait.until(current -> current.findElement(LOGIN_FORM));
        TestConfig.requireSameOrigin(form.getDomProperty("action"));
        if (!"post".equalsIgnoreCase(form.getDomProperty("method"))) {
            throw new IllegalStateException("Account login form must use POST.");
        }
        WebElement usernameInput = form.findElement(By.id("username"));
        usernameInput.clear();
        usernameInput.sendKeys(username);
        WebElement passwordInput = form.findElement(By.id("password"));
        passwordInput.clear();
        passwordInput.sendKeys(password);
        WebElement rememberInput = form.findElement(By.id("rememberme"));
        if (rememberInput.isSelected() != remember) {
            rememberInput.click();
        }
    }

    public void submitLogin() {
        TestConfig.requireAccountLoginAllowed();
        TestConfig.requireSameOrigin(driver.getCurrentUrl());
        WebElement form = driver.findElement(LOGIN_FORM);
        TestConfig.requireSameOrigin(form.getDomProperty("action"));
        if (!"post".equalsIgnoreCase(form.getDomProperty("method"))) {
            throw new IllegalStateException("Account login form must use POST.");
        }
        form.findElement(By.cssSelector("button[name='login']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".woocommerce-MyAccount-content")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".woocommerce-error"))));
    }

    public boolean loginPasswordMasked() {
        return "password".equals(driver.findElement(LOGIN_FORM).findElement(By.id("password"))
                .getDomProperty("type"));
    }

    public void waitForDashboard(String username) {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(current -> {
            URI actual = URI.create(current.getCurrentUrl());
            URI expected = TestConfig.baseUrl().resolve("/account/");
            if (!expected.getScheme().equalsIgnoreCase(actual.getScheme())
                    || !expected.getHost().equalsIgnoreCase(actual.getHost())
                    || expected.getPort() != actual.getPort()
                    || !"/account".equals(actual.getPath().replaceAll("/+$", ""))) {
                return false;
            }
            return current.findElements(By.cssSelector(".woocommerce-MyAccount-content")).stream()
                    .anyMatch(content -> content.isDisplayed()
                            && content.getText().replaceAll("\\s+", " ").contains("Hello " + username))
                    && current.findElements(LOGIN_FORM).stream().noneMatch(WebElement::isDisplayed);
        });
    }

    public boolean loginFormVisible() {
        return driver.findElements(LOGIN_FORM).stream().anyMatch(WebElement::isDisplayed);
    }

    public boolean loginErrorVisible() {
        return driver.findElements(By.cssSelector(".woocommerce-error")).stream()
                .anyMatch(WebElement::isDisplayed);
    }

    public boolean registrationFormVisible() {
        return driver.findElements(REGISTRATION_FORM).stream().anyMatch(WebElement::isDisplayed);
    }

    public void enterRegistration(String username, String email, String password) {
        TestConfig.requireAccountRegistrationAllowed();
        TestConfig.requireSameOrigin(driver.getCurrentUrl());
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement form = wait.until(current -> current.findElement(REGISTRATION_FORM));
        TestConfig.requireSameOrigin(form.getDomProperty("action"));
        if (!"post".equalsIgnoreCase(form.getDomProperty("method"))) {
            throw new IllegalStateException("Account registration form must use POST.");
        }
        setValue(form, "#reg_username", username);
        setValue(form, "#reg_email", email);
        setValue(form, "#reg_password", password);
    }

    public void clearRegistration() {
        enterRegistration("", "", "");
    }

    public void submitRegistration() {
        TestConfig.requireAccountRegistrationAllowed();
        TestConfig.requireSameOrigin(driver.getCurrentUrl());
        WebElement form = driver.findElement(REGISTRATION_FORM);
        TestConfig.requireSameOrigin(form.getDomProperty("action"));
        if (!"post".equalsIgnoreCase(form.getDomProperty("method"))) {
            throw new IllegalStateException("Account registration form must use POST.");
        }
        WebElement button = form.findElement(By.cssSelector("button[name='register']"));
        boolean nativeValidationBlocksSubmission = !"true".equals(form.getDomProperty("noValidate"))
                && !"true".equals(button.getDomProperty("formNoValidate"))
                && !form.findElement(By.id("reg_email")).getDomProperty("validationMessage").isBlank();
        button.click();
        if (nativeValidationBlocksSubmission) {
            return;
        }
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".woocommerce-MyAccount-content")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".woocommerce-error"))));
    }

    public String registrationValidationMessage() {
        String nativeMessage = driver.findElement(REGISTRATION_FORM)
                .findElement(By.id("reg_email")).getDomProperty("validationMessage");
        return nativeMessage.isBlank() ? registrationError() : nativeMessage;
    }

    public String registrationError() {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".woocommerce-error li")))
                .getText().trim().replaceAll("\\s+", " ");
    }

    public boolean accountNavigationContains(List<String> labels) {
        List<String> visibleLabels = driver.findElements(
                        By.cssSelector(".woocommerce-MyAccount-navigation-link a")).stream()
                .filter(WebElement::isDisplayed)
                .map(element -> element.getText().trim().replaceAll("\\s+", " "))
                .toList();
        return visibleLabels.containsAll(labels);
    }

    public void logout() {
        TestConfig.requireAccountLoginAllowed();
        WebElement link = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".woocommerce-MyAccount-navigation-link--customer-logout a")));
        TestConfig.requireSameOrigin(link.getDomProperty("href"));
        link.click();
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(current -> loginFormVisible());
    }

    private void setValue(WebElement form, String selector, String value) {
        WebElement input = form.findElement(By.cssSelector(selector));
        input.clear();
        if (!value.isEmpty()) {
            input.sendKeys(value);
        }
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
