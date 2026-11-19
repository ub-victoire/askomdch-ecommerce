package com.askomdch.qa.hooks;

import com.askomdch.qa.config.TestConfig;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class BrowserHooks {
    private WebDriver driver;
    private Path browserProfile;

    @Before(order = -1, value = "@pending")
    public void rejectPendingScenario() {
        throw new IllegalStateException("Pending scenario has no approved implementation or test fixtures.");
    }

    @Before(order = 0, value = "@cart_session")
    public void authorizeCartChanges() {
        TestConfig.requireCartChangesAllowed();
        TestConfig.cartProductUrl();
    }

    @Before(order = 0, value = "@checkout_login")
    public void authorizeCheckoutLogin() {
        TestConfig.requireCheckoutLoginAllowed();
    }

    @Before(order = 0, value = "@checkout_order")
    public void authorizeCheckoutOrder() {
        TestConfig.requireCheckoutOrderAllowed();
        TestConfig.cartProductUrl();
    }

    @Before(order = 1)
    public void startBrowser() {
        TestConfig.baseUrl();
        if (browserProfile == null) {
            try {
                browserProfile = Files.createTempDirectory("askomdch-checkout-browser-");
            } catch (IOException exception) {
                throw new IllegalStateException("Could not create an isolated browser profile.", exception);
            }
        }
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1280,900",
                "--user-data-dir=" + browserProfile.toAbsolutePath());
        driver = new ChromeDriver(options);
    }

    @After
    public void stopBrowser() {
        closeBrowser();
        if (browserProfile != null) {
            try (var paths = Files.walk(browserProfile)) {
                paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException exception) {
                        throw new IllegalStateException("Could not remove isolated browser profile.", exception);
                    }
                });
            } catch (IOException exception) {
                throw new IllegalStateException("Could not remove isolated browser profile.", exception);
            } finally {
                browserProfile = null;
            }
        }
    }

    public void reopenBrowser() {
        closeBrowser();
        startBrowser();
    }

    private void closeBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    public WebDriver driver() {
        if (driver == null) {
            throw new IllegalStateException("Browser was not started.");
        }
        return driver;
    }
}
