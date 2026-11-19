package com.askomdch.qa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.askomdch.qa.hooks.BrowserHooks;
import com.askomdch.qa.pages.RecoveryPage;
import com.sun.net.httpserver.HttpServer;
import io.cucumber.core.cli.Main;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.TimeoutException;

public class LocalFixtureTest {
    private HttpServer server;
    private String previousBaseUrl;
    private volatile boolean showRecoveryInstruction;
    private volatile boolean wrongSearchResults;
    private volatile boolean wrongProductTitle;
    private volatile boolean showCartContents;

    @Before
    public void startFixture() throws IOException {
        previousBaseUrl = System.getProperty("baseUrl");
        showRecoveryInstruction = true;
        wrongSearchResults = false;
        wrongProductTitle = false;
        showCartContents = false;
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/account/", exchange -> {
            String html;
            if (exchange.getRequestURI().getPath().equals("/account/lost-password/")) {
                html = """
                        <form class="lost_reset_password">
                          %s
                          <p><label for="user_login">Username or email</label>
                          <input id="user_login"></p>
                          <button type="submit">Reset password</button>
                        </form>
                        """.formatted(showRecoveryInstruction ? "<p>Recovery instructions</p>" : "");
            } else {
                html = """
                        <form class="woocommerce-form-login">
                          <input id="username">
                          <input id="password">
                          <label class="woocommerce-form-login__rememberme">
                            <input type="checkbox" id="rememberme">Remember me
                          </label>
                          <button name="login">Log in</button>
                        </form>
                        <form class="woocommerce-form-register">
                          <input id="reg_username">
                          <input id="reg_email">
                          <input id="reg_password">
                          <button name="register">Register</button>
                        </form>
                        """;
            }
            byte[] body = ("<!doctype html><html><body>" + html + "</body></html>")
                    .getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, body.length);
            try (var output = exchange.getResponseBody()) {
                output.write(body);
            }
        });
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String html;
            if (path.equals("/store/")) {
                html = """
                        <form class="woocommerce-product-search" action="/store/" method="get">
                          <input type="search" name="s">
                          <button type="submit">Search</button>
                        </form>
                        <ul class="products">
                          <li class="product">
                            <a class="woocommerce-LoopProduct-link" href="/product/blue-tshirt/">
                              <h2 class="woocommerce-loop-product__title">Blue Tshirt</h2>
                            </a>
                          </li>
                          %s
                        </ul>
                        """.formatted(wrongSearchResults ? "" : """
                          <li class="product">
                            <a class="woocommerce-LoopProduct-link" href="/product/green-tshirt/">
                              <h2 class="woocommerce-loop-product__title">Green Tshirt</h2>
                            </a>
                          </li>
                        """);
            } else if (path.equals("/product/blue-tshirt/")) {
                html = "<h1 class=\"product_title\">"
                        + (wrongProductTitle ? "Incorrect product" : "Blue Tshirt") + "</h1>";
            } else if (path.equals("/cart/")) {
                html = """
                        <p class="cart-empty">Your cart is currently empty.</p>
                        <p class="return-to-shop"><a class="wc-backward" href="/store/">RETURN TO SHOP</a></p>
                        %s
                        """.formatted(showCartContents ? "<div class=\"cart_totals\">Unexpected totals</div>" : "");
            } else {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }
            byte[] body = ("<!doctype html><html><body>" + html + "</body></html>")
                    .getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, body.length);
            try (var output = exchange.getResponseBody()) {
                output.write(body);
            }
        });
        server.start();
        System.setProperty("baseUrl", "http://127.0.0.1:" + server.getAddress().getPort());
    }

    @After
    public void stopFixture() {
        if (server != null) {
            server.stop(0);
        }
        if (previousBaseUrl == null) {
            System.clearProperty("baseUrl");
        } else {
            System.setProperty("baseUrl", previousBaseUrl);
        }
    }

    @Test
    public void scenariosRunAgainstLocalFixture() {
        assertEquals(0, runCucumber("features"));
    }

    @Test
    public void recoveryCheckRejectsMissingInstruction() {
        showRecoveryInstruction = false;
        BrowserHooks browser = new BrowserHooks();
        try {
            browser.startBrowser();
            browser.driver().get(System.getProperty("baseUrl") + "/account/lost-password/");
            assertThrows(TimeoutException.class,
                    () -> new RecoveryPage(browser.driver()).instructionAndControlsVisible());
        } finally {
            browser.stopBrowser();
        }
    }

    @Test
    public void searchScenarioFailsWhenARequiredProductIsMissing() {
        wrongSearchResults = true;
        assertEquals(1, runCucumber("features/product-browsing.feature:15", "@AEC-18"));
    }

    @Test
    public void productScenarioFailsForAnIncorrectDetailTitle() {
        wrongProductTitle = true;
        assertEquals(1, runCucumber("features/product-browsing.feature:21", "@AEC-110"));
    }

    @Test
    public void emptyCartScenarioFailsWhenTotalsAreVisible() {
        showCartContents = true;
        assertEquals(1, runCucumber("features/cart.feature", "@AEC-112"));
    }

    private byte runCucumber(String features) {
        return runCucumber(features, "not @unused");
    }

    private byte runCucumber(String features, String tags) {
        return Main.run(new String[] {
                "--glue", "com.askomdch.qa.hooks",
                "--glue", "com.askomdch.qa.steps",
                "--plugin", "summary",
                "--tags", tags,
                features
        }, Thread.currentThread().getContextClassLoader());
    }
}
