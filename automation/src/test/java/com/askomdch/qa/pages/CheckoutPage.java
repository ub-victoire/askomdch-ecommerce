package com.askomdch.qa.pages;

import com.askomdch.qa.config.TestConfig;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CheckoutPage {
    private static final By LOGIN_FORM = By.cssSelector("form.login");
    private static final By LOGIN_TOGGLE = By.cssSelector(".woocommerce-info a.showlogin");
    private static final By PASSWORD = By.cssSelector("form.login input[name='password']");
    private static final By CREATE_ACCOUNT = By.id("createaccount");
    private static final By ACCOUNT_PASSWORD = By.id("account_password");
    private final WebDriver driver;
    private final WebDriverWait wait;

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public String loginBanner() {
        WebElement toggle = wait.until(ExpectedConditions.visibilityOfElementLocated(LOGIN_TOGGLE));
        return toggle.findElement(By.xpath("./ancestor::*[contains(concat(' ', normalize-space(@class), ' '), "
                + "' woocommerce-info ')][1]")).getText().trim().replaceAll("\\s+", " ");
    }

    public void toggleLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(LOGIN_TOGGLE)).click();
    }

    public boolean loginFormVisible() {
        return driver.findElements(LOGIN_FORM).stream().anyMatch(WebElement::isDisplayed);
    }

    public void waitForLoginForm(boolean visible) {
        wait.until(current -> loginFormVisible() == visible);
    }

    public String loginPasswordType() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(PASSWORD)).getDomProperty("type");
    }

    public void openRecovery() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("form.login .lost_password a")));
        TestConfig.requireSameOrigin(link.getDomProperty("href"));
        link.click();
    }

    public void submitWithoutUsername(String syntheticPassword) {
        TestConfig.requireCheckoutLoginAllowed();
        TestConfig.requireSameOrigin(driver.getCurrentUrl());
        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(LOGIN_FORM));
        TestConfig.requireSameOrigin(form.getDomProperty("action"));
        if (!"post".equalsIgnoreCase(form.getDomProperty("method"))) {
            throw new IllegalStateException("Checkout login must use POST before submitting test input.");
        }
        WebElement username = form.findElement(By.cssSelector("input[name='username']"));
        username.sendKeys(Keys.CONTROL, "a", Keys.BACK_SPACE);
        WebElement password = form.findElement(By.cssSelector("input[name='password']"));
        password.sendKeys(Keys.CONTROL, "a", Keys.BACK_SPACE);
        password.sendKeys(syntheticPassword);
        wait.until(ExpectedConditions.elementToBeClickable(
                form.findElement(By.cssSelector("button[name='login']")))).click();
    }

    public void submitWithoutPassword(String username) {
        TestConfig.requireCheckoutLoginAllowed();
        TestConfig.requireSameOrigin(driver.getCurrentUrl());
        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(LOGIN_FORM));
        form.findElement(By.cssSelector("input[name='username']")).sendKeys(username);
        WebElement password = form.findElement(By.cssSelector("input[name='password']"));
        password.clear();
        TestConfig.requireSameOrigin(form.getDomProperty("action"));
        if (!"post".equalsIgnoreCase(form.getDomProperty("method"))) {
            throw new IllegalStateException("Checkout login must use POST before submitting test input.");
        }
        form.findElement(By.cssSelector("button[name='login']")).click();
    }

    public String loginError() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".woocommerce-error li"))).getText().trim().replaceAll("\\s+", " ");
    }

    public boolean loginRecoveryLinkVisible() {
        return driver.findElements(By.cssSelector("form.login .lost_password a"))
                .stream().anyMatch(WebElement::isDisplayed);
    }

    public boolean loginErrorRecoveryLinkVisible() {
        return driver.findElements(By.cssSelector(".woocommerce-error li a"))
                .stream().anyMatch(link -> link.isDisplayed()
                        && link.getText().trim().equals("Lost your password?"));
    }

    public void login(String username, String password, boolean remember) {
        TestConfig.requireCheckoutLoginAllowed();
        TestConfig.requireSameOrigin(driver.getCurrentUrl());
        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(LOGIN_FORM));
        form.findElement(By.cssSelector("input[name='username']")).sendKeys(username);
        form.findElement(By.cssSelector("input[name='password']")).sendKeys(password);
        List<WebElement> rememberCheckbox = form.findElements(By.cssSelector("input[name='rememberme']"));
        if (rememberCheckbox.isEmpty()) {
            throw new IllegalStateException("Checkout login Remember me control was not found.");
        }
        if (rememberCheckbox.get(0).isSelected() != remember) {
            rememberCheckbox.get(0).click();
        }
        TestConfig.requireSameOrigin(form.getDomProperty("action"));
        if (!"post".equalsIgnoreCase(form.getDomProperty("method"))) {
            throw new IllegalStateException("Checkout login must use POST.");
        }
        form.findElement(By.cssSelector("button[name='login']")).click();
    }

    public void fillBilling(Map<String, String> values) {
        values.forEach(this::fillField);
    }

    public void fillShipping(Map<String, String> values) {
        WebElement toggle = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("ship-to-different-address-checkbox")));
        if (!toggle.isSelected()) {
            toggle.click();
        }
        values.forEach(this::fillField);
    }

    public void fillAccountPassword(String password) {
        WebElement create = wait.until(ExpectedConditions.elementToBeClickable(CREATE_ACCOUNT));
        if (!create.isSelected()) {
            create.click();
        }
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(ACCOUNT_PASSWORD));
        field.sendKeys(password);
    }

    public void fillField(String id, String value) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
        if ("select".equalsIgnoreCase(field.getTagName())) {
            new Select(field).selectByValue(value);
        } else {
            field.clear();
            field.sendKeys(value);
        }
    }

    public void clearField(String id) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
        if ("select".equalsIgnoreCase(field.getTagName())) {
            new Select(field).selectByValue("");
        } else {
            field.clear();
        }
    }

    public void submitOrder() {
        TestConfig.requireCheckoutOrderAllowed();
        TestConfig.requireSameOrigin(driver.getCurrentUrl());
        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("form.checkout")));
        TestConfig.requireSameOrigin(form.getDomProperty("action"));
        if (!"post".equalsIgnoreCase(form.getDomProperty("method"))) {
            throw new IllegalStateException("Checkout order must use POST.");
        }
        wait.until(ExpectedConditions.elementToBeClickable(By.id("place_order"))).click();
    }

    public String checkoutErrors() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".woocommerce-error"))).getText().trim().replaceAll("\\s+", " ");
    }

    public boolean fieldInvalid(String fieldId) {
        WebElement field = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(fieldId)));
        return field.getAttribute("class").contains("woocommerce-invalid")
                || field.findElement(By.xpath("./ancestor::*[contains(@class,'form-row')]"))
                        .getAttribute("class").contains("woocommerce-invalid");
    }

    public String orderReceivedShippingAddress() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".woocommerce-customer-details .woocommerce-column--shipping-address")))
                .getText().trim().replaceAll("\\s+", " ");
    }

    public List<String> shippingAddressValues() {
        return List.of("shipping_first_name", "shipping_last_name", "shipping_country",
                "shipping_address_1", "shipping_city", "shipping_state", "shipping_postcode").stream()
                .map(id -> {
                    WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
                    return "select".equalsIgnoreCase(field.getTagName())
                            ? new Select(field).getFirstSelectedOption().getText().trim()
                            : field.getDomProperty("value").trim();
                }).toList();
    }

    public void selectPaymentMethod(String method) {
        By option = paymentOption(method);
        wait.until(ExpectedConditions.elementToBeClickable(option)).click();
        wait.until(current -> current.findElement(option).isSelected());
    }

    public void selectDefaultBankTransfer() {
        wait.until(current -> current.findElement(paymentOption("direct bank transfer")).isSelected());
    }

    public boolean checkoutErrorContains(String expected) {
        return checkoutErrors().contains(expected);
    }

    public boolean orderReceived() {
        return wait.until(current -> {
            TestConfig.requireSameOrigin(current.getCurrentUrl());
            return java.net.URI.create(current.getCurrentUrl()).getPath()
                    .matches("/checkout/order-received/[^/]+/?")
                    && current.findElements(By.cssSelector(".woocommerce-order-overview__order.order strong"))
                            .stream().anyMatch(element -> element.isDisplayed() && !element.getText().isBlank());
        });
    }

    public boolean fieldInvalidById(String fieldId) {
        WebElement wrapper = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(fieldId + "_field")));
        return wrapper.getAttribute("class").contains("woocommerce-invalid");
    }

    public String billingFieldId(String field) {
        return fieldId(field, "billing");
    }

    public String shippingFieldId(String field) {
        return fieldId(field, "shipping");
    }

    private String fieldId(String field, String prefix) {
        String suffix = switch (field) {
            case "First Name" -> "first_name";
            case "Last Name" -> "last_name";
            case "Country / Region" -> "country";
            case "Street Address" -> "address_1";
            case "Town / City" -> "city";
            case "State / County" -> "state";
            case "Postcode / ZIP" -> "postcode";
            case "Phone" -> "phone";
            case "Email Address" -> "email";
            default -> throw new IllegalArgumentException("Unknown checkout field: " + field);
        };
        return prefix + "_" + suffix;
    }

    public String invalidFieldValue(String field) {
        return switch (field) {
            case "First Name", "Last Name", "Town / City" -> "QA-Invalid";
            default -> throw new IllegalArgumentException("Field does not have an alphabetical-only rule: " + field);
        };
    }


    public boolean accountSelected() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(CREATE_ACCOUNT)).isSelected();
    }

    public boolean accountPasswordVisible() {
        return driver.findElements(ACCOUNT_PASSWORD).stream().anyMatch(WebElement::isDisplayed);
    }

    public void setAccountCreation(boolean enabled) {
        WebElement checkbox = wait.until(ExpectedConditions.elementToBeClickable(CREATE_ACCOUNT));
        if (checkbox.isSelected() != enabled) {
            checkbox.click();
        }
        wait.until(current -> accountPasswordVisible() == enabled);
    }

    public void selectPayment(String method) {
        By option = paymentOption(method);
        wait.until(ExpectedConditions.elementToBeClickable(option)).click();
        wait.until(current -> current.findElement(option).isSelected()
                && current.findElement(paymentBox(method)).isDisplayed());
    }

    public boolean selectedPaymentAndInstructions(String selected, String other) {
        WebElement chosen = wait.until(ExpectedConditions.visibilityOfElementLocated(paymentOption(selected)));
        WebElement notChosen = wait.until(ExpectedConditions.visibilityOfElementLocated(paymentOption(other)));
        wait.until(current -> current.findElement(paymentBox(selected)).isDisplayed()
                && !current.findElement(paymentBox(other)).isDisplayed());
        long selectedCount = driver.findElements(By.cssSelector("#payment input[name='payment_method']"))
                .stream().filter(WebElement::isSelected).count();
        return chosen.isSelected() && !notChosen.isSelected() && selectedCount == 1;
    }

    public Review orderReview() {
        String title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("order_review_heading")))
                .getText().trim();
        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".woocommerce-checkout-review-order-table")));
        List<String> headers = table.findElements(By.cssSelector("thead th")).stream()
                .map(element -> element.getText().trim()).toList();
        List<WebElement> rows = table.findElements(By.cssSelector("tbody tr.cart_item"));
        if (rows.size() != 1) {
            throw new IllegalStateException("Order-review check requires exactly one cart item.");
        }
        WebElement row = rows.get(0);
        String product = row.findElement(By.cssSelector(".product-name")).getText()
                .trim().replaceAll("\\s+", " ");
        BigDecimal subtotal = UsdPrice.parse(
                row.findElement(By.cssSelector(".product-total .woocommerce-Price-amount")).getText());
        return new Review(title, headers, product, subtotal);
    }

    private By paymentOption(String method) {
        return By.id("payment_method_" + paymentCode(method));
    }

    private By paymentBox(String method) {
        return By.cssSelector(".payment_box.payment_method_" + paymentCode(method));
    }

    private String paymentCode(String method) {
        return switch (method) {
            case "direct bank transfer" -> "bacs";
            case "cash on delivery" -> "cod";
            default -> throw new IllegalArgumentException("Unknown payment method: " + method);
        };
    }

    public record Review(String title, List<String> headers, String product, BigDecimal subtotal) {
    }
}
