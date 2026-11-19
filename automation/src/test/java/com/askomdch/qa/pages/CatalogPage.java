package com.askomdch.qa.pages;

import com.askomdch.qa.config.TestConfig;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CatalogPage {
    private static final By SEARCH_FIELD =
            By.cssSelector("form.woocommerce-product-search input[name='s']");
    private static final By PRODUCTS = By.cssSelector("ul.products li.product");
    private static final By TITLE = By.cssSelector(".woocommerce-loop-product__title");
    private static final By PRODUCT_LINK = By.cssSelector("a.woocommerce-LoopProduct-link");
    private static final By SORTING = By.cssSelector("form.woocommerce-ordering select[name='orderby']");

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

    public String selectedSortingOption() {
        return sorting().getFirstSelectedOption().getText().trim();
    }

    public List<String> sortingOptions() {
        return sorting().getOptions().stream().map(option -> option.getText().trim()).toList();
    }

    public void openMenCategory() {
        openHeaderCategory("Men");
    }

    public void openHeaderCategory(String category) {
        if (!List.of("Men", "Women", "Accessories").contains(category)) {
            throw new IllegalArgumentException("Unknown documented category: " + category);
        }
        String path = "/product-category/" + category.toLowerCase(Locale.ROOT);
        URI expected = TestConfig.baseUrl().resolve(path + "/");
        WebElement link = wait.until(current -> current.findElements(By.cssSelector("header a[href]")).stream()
                .filter(WebElement::isDisplayed)
                .filter(WebElement::isEnabled)
                .filter(element -> {
                    URI target = URI.create(element.getDomProperty("href"));
                    return expected.getScheme().equalsIgnoreCase(target.getScheme())
                            && expected.getHost().equalsIgnoreCase(target.getHost())
                            && expected.getPort() == target.getPort()
                            && path.equals(target.getPath().replaceAll("/+$", ""));
                }).findFirst().orElse(null));
        link.click();
        wait.until(current -> {
            URI actual = URI.create(current.getCurrentUrl());
            return expected.getScheme().equalsIgnoreCase(actual.getScheme())
                    && expected.getHost().equalsIgnoreCase(actual.getHost())
                    && expected.getPort() == actual.getPort()
                    && actual.getPath().replaceAll("/+$", "").equals(path);
        });
    }

    public String categoryHeading() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("h1.woocommerce-products-header__title"))).getText().trim();
    }

    public void selectSorting(String label) {
        Select select = sorting();
        String value = select.getOptions().stream()
                .filter(option -> option.getText().trim().equals(label))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Sorting option missing: " + label))
                .getDomAttribute("value");
        select.selectByVisibleText(label);
        wait.until(current -> {
            String query = URI.create(current.getCurrentUrl()).getRawQuery();
            return query != null && Arrays.stream(query.split("&"))
                    .map(part -> part.split("=", 2))
                    .anyMatch(part -> part.length == 2
                            && URLDecoder.decode(part[0], StandardCharsets.UTF_8).equals("orderby")
                            && URLDecoder.decode(part[1], StandardCharsets.UTF_8).equals(value));
        });
        wait.until(current -> sorting().getFirstSelectedOption().getText().trim().equals(label));
    }

    public List<BigDecimal> visiblePrices() {
        if (driver.findElements(By.cssSelector("nav.woocommerce-pagination")).stream().anyMatch(WebElement::isDisplayed)) {
            throw new IllegalStateException("Price sorting requires a category that fits on one page.");
        }
        List<WebElement> products = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(PRODUCTS));
        return products.stream().map(product -> {
            List<WebElement> sale = product.findElements(By.cssSelector(".price ins .woocommerce-Price-amount"));
            List<WebElement> amounts = sale.isEmpty()
                    ? product.findElements(By.cssSelector(".price .woocommerce-Price-amount")) : sale;
            if (amounts.size() != 1) {
                throw new IllegalStateException("Expected one current price per product; price range is ambiguous.");
            }
            return UsdPrice.parse(amounts.get(0).getText());
        }).toList();
    }

    private Select sorting() {
        return new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(SORTING)));
    }

    public record SelectedProduct(String title, String url) {
    }
}
