package com.askomdch.qa.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
}
