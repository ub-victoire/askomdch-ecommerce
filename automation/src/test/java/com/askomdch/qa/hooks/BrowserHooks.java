package com.askomdch.qa.hooks;

import com.askomdch.qa.config.TestConfig;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class BrowserHooks {
    private WebDriver driver;

    @Before
    public void startBrowser() {
        TestConfig.baseUrl();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1280,900");
        driver = new ChromeDriver(options);
    }

    @After
    public void stopBrowser() {
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
