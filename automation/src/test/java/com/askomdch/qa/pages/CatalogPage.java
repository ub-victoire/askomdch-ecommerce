package com.askomdch.qa.pages;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CatalogPage {
    private static final By SEARCH_FIELD =
            By.cssSelector("form.woocommerce-product-search input[name='s']");
    private static final By PRODUCTS = By.cssSelector("ul.products li.product");
    private static final By TITLE = By.cssSelector(".woocommerce-loop-product__title");
    private static final By PRODUCT_LINK = By.cssSelector("a.woocommerce-LoopProduct-link");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public CatalogPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void search(String query) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_FIELD));
        input.clear();
        input.sendKeys(query, Keys.ENTER);
        wait.until(current -> {
            String rawQuery = URI.create(current.getCurrentUrl()).getRawQuery();
            if (rawQuery == null) {
                return false;
            }
            return Arrays.stream(rawQuery.split("&"))
                    .map(part -> part.split("=", 2))
                    .anyMatch(part -> part.length == 2
                            && URLDecoder.decode(part[0], StandardCharsets.UTF_8).equals("s")
                            && URLDecoder.decode(part[1], StandardCharsets.UTF_8).equals(query));
        });
    }

    public List<String> productNames() {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.cssSelector("ul.products li.product .woocommerce-loop-product__title")))
                .stream().map(WebElement::getText).toList();
    }

    public SelectedProduct openFirstProduct() {
        WebElement product = wait.until(ExpectedConditions.visibilityOfElementLocated(PRODUCTS));
        String title = product.findElement(TITLE).getText().trim();
        WebElement link = product.findElement(PRODUCT_LINK);
        String url = link.getDomProperty("href");
        if (title.isBlank() || url == null || url.isBlank()) {
            throw new IllegalStateException("The selected product must have a title and detail link.");
        }
        link.click();
        return new SelectedProduct(title, url);
    }

    public record SelectedProduct(String title, String url) {
    }
}
