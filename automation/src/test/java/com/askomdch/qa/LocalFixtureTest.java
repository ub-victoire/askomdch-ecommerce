package com.askomdch.qa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.askomdch.qa.hooks.BrowserHooks;
import com.askomdch.qa.pages.RecoveryPage;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
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
    private String previousCartOptIn;
    private String previousProductPath;
    private String previousLoginOptIn;
    private String previousOrderOptIn;
    private volatile boolean showRecoveryInstruction;
    private volatile boolean wrongSearchResults;
    private volatile boolean wrongProductTitle;
    private volatile boolean showCartContents;
    private volatile boolean missingSortingOption;
    private volatile boolean incorrectPriceSort;
    private volatile boolean wrongCategoryHeading;
    private volatile boolean wrongCartSubtotal;
    private volatile boolean unmaskedCheckoutPassword;
    private volatile boolean wrongCheckoutSubtotal;
    private volatile boolean wrongCheckoutLoginError;

    @Before
    public void startFixture() throws IOException {
        previousBaseUrl = System.getProperty("baseUrl");
        previousCartOptIn = System.getProperty("allowCartChanges");
        previousProductPath = System.getProperty("cartProductPath");
        previousLoginOptIn = System.getProperty("allowCheckoutLogin");
        previousOrderOptIn = System.getProperty("allowCheckoutOrder");
        showRecoveryInstruction = true;
        wrongSearchResults = false;
        wrongProductTitle = false;
        showCartContents = false;
        missingSortingOption = false;
        incorrectPriceSort = false;
        wrongCategoryHeading = false;
        wrongCartSubtotal = false;
        unmaskedCheckoutPassword = false;
        wrongCheckoutSubtotal = false;
        wrongCheckoutLoginError = false;
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
                        <header>
                          <a href="/product-category/men/">Men</a>
                          <a href="/product-category/women/">Women</a>
                          <a href="/product-category/accessories/">Accessories</a>
                        </header>
                        %s
                        <ul class="products">
                          <li class="product">
                            <a class="woocommerce-LoopProduct-link" href="/product/blue-tshirt/">
                              <h2 class="woocommerce-loop-product__title">Blue Tshirt</h2>
                            </a>
                          </li>
                          %s
                        </ul>
                        """.formatted(sortingMarkup("menu_order"), wrongSearchResults ? "" : """
                          <li class="product">
                            <a class="woocommerce-LoopProduct-link" href="/product/green-tshirt/">
                              <h2 class="woocommerce-loop-product__title">Green Tshirt</h2>
                            </a>
                          </li>
                        """);
            } else if (path.equals("/product-category/men/")) {
                String query = exchange.getRequestURI().getRawQuery();
                boolean descending = query != null && query.contains("orderby=price-desc");
                String first = """
                        <li class="product"><span class="price">
                          <span class="woocommerce-Price-amount">$10.00</span>
                        </span></li>
                        """;
                String second = """
                        <li class="product"><span class="price">
                          <span class="woocommerce-Price-amount">$20.00</span>
                        </span></li>
                        """;
                html = categoryHeading("Men")
                        + sortingMarkup(query == null ? "menu_order" : descending ? "price-desc" : "price")
                        + "<ul class=\"products\">"
                        + (descending && !incorrectPriceSort ? second + first : first + second) + "</ul>";
            } else if (path.equals("/product-category/women/")) {
                html = categoryHeading("Women");
            } else if (path.equals("/product-category/accessories/")) {
                html = categoryHeading("Accessories");
            } else if (path.equals("/product/blue-tshirt/")) {
                html = "<h1 class=\"product_title\">"
                        + (wrongProductTitle ? "Incorrect product" : "Blue Tshirt") + "</h1>"
                        + (exchange.getRequestURI().getRawQuery() == null ? "" : "<p class=\"woocommerce-message\">Added</p>")
                        + """
                          <form class="cart" action="/add-cart/" method="get">
                            <button name="add-to-cart" value="1">Add to cart</button>
                          </form>
                          """;
            } else if (path.equals("/add-cart/")) {
                redirectWithCartQuantity(exchange, 1, "/product/blue-tshirt/?added=1");
                return;
            } else if (path.equals("/update-cart/")) {
                String query = exchange.getRequestURI().getRawQuery();
                if (query == null || !query.matches("quantity=(0|[1-9][0-9]*)(&.*)?")) {
                    exchange.sendResponseHeaders(400, -1);
                    exchange.close();
                    return;
                }
                int quantity = Integer.parseInt(query.substring("quantity=".length()).split("&")[0]);
                redirectWithCartQuantity(exchange, quantity, "/cart/?updated=1");
                return;
            } else if (path.equals("/remove-cart/")) {
                redirectWithCartQuantity(exchange, 0, "/cart/");
                return;
            } else if (path.equals("/checkout/")) {
                if (cartQuantity(exchange) != 1) {
                    exchange.getResponseHeaders().set("Location", "/cart/");
                    exchange.sendResponseHeaders(303, -1);
                    exchange.close();
                    return;
                }
                if (exchange.getRequestMethod().equals("POST")) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    if (!body.startsWith("username=&password=")) {
                        exchange.sendResponseHeaders(400, -1);
                        exchange.close();
                        return;
                    }
                    html = "<ul class=\"woocommerce-error\"><li>"
                            + (wrongCheckoutLoginError ? "Incorrect fixture error" : "Error: Username is required")
                            + "</li></ul>" + checkoutMarkup();
                } else {
                    html = checkoutMarkup();
                }
            } else if (path.equals("/cart/")) {
                int quantity = cartQuantity(exchange);
                html = quantity > 0 ? cartMarkup(quantity, exchange.getRequestURI().getRawQuery() != null) : """
                        %s
                        <p class="cart-empty">Your cart is currently empty.</p>
                        <p class="return-to-shop"><a class="wc-backward" href="/store/">RETURN TO SHOP</a></p>
                        %s
                        """.formatted(exchange.getRequestURI().getRawQuery() != null
                                ? "<div class=\"woocommerce-message\">Cart updated.</div>" : "",
                                showCartContents ? "<div class=\"cart_totals\">Unexpected totals</div>" : "");
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

    private int cartQuantity(HttpExchange exchange) {
        String cookies = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookies != null) {
            for (String cookie : cookies.split(";\\s*")) {
                if (cookie.startsWith("fixture_cart=")) {
                    return Integer.parseInt(cookie.substring("fixture_cart=".length()));
                }
            }
        }
        return 0;
    }

    private String checkoutMarkup() {
        return """
                <div class="woocommerce-info">Returning customer?
                  <a class="showlogin" href="#" onclick="let f = document.querySelector('form.login');
                    f.style.display = f.style.display === 'none' ? 'block' : 'none'; return false;">Click here to login</a>
                </div>
                <form class="login" action="/checkout/" method="post" style="display:none">
                  <input name="username">
                  <input name="password" type="%s">
                  <label><input type="checkbox" name="rememberme" value="forever">Remember me</label>
                  <button name="login" value="login">Login</button>
                  <p class="lost_password"><a href="/account/lost-password/">Lost your password?</a></p>
                </form>
                <label><input id="createaccount" type="checkbox"
                  onchange="document.getElementById('account_password').style.display = this.checked ? 'block' : 'none'">
                  Create an account?</label>
                <input id="account_password" type="password" style="display:none">
                <h3 id="order_review_heading">Your order</h3>
                <table class="woocommerce-checkout-review-order-table">
                  <thead><tr><th>Product</th><th>Subtotal</th></tr></thead>
                  <tbody><tr class="cart_item"><td class="product-name">Blue Tshirt × 1</td>
                    <td class="product-total"><span class="woocommerce-Price-amount">$%s</span></td>
                  </tr></tbody>
                </table>
                <div id="payment">
                  <input type="radio" name="payment_method" id="payment_method_bacs" checked
                    onclick="document.querySelector('.payment_method_bacs').style.display='block';
                      document.querySelector('.payment_method_cod').style.display='none'">
                  <div class="payment_box payment_method_bacs">Bank transfer instructions</div>
                  <input type="radio" name="payment_method" id="payment_method_cod"
                    onclick="document.querySelector('.payment_method_cod').style.display='block';
                      document.querySelector('.payment_method_bacs').style.display='none'">
                  <div class="payment_box payment_method_cod" style="display:none">Cash instructions</div>
                </div>
                """.formatted(unmaskedCheckoutPassword ? "text" : "password",
                        wrongCheckoutSubtotal ? "11.00" : "10.00");
    }

    private void redirectWithCartQuantity(HttpExchange exchange, int quantity, String destination) throws IOException {
        exchange.getResponseHeaders().add("Set-Cookie", "fixture_cart=" + quantity + "; Path=/");
        exchange.getResponseHeaders().set("Location", destination);
        exchange.sendResponseHeaders(303, -1);
        exchange.close();
    }

    private String cartMarkup(int quantity, boolean updated) {
        return """
                %s
                <form class="woocommerce-cart-form" action="/update-cart/" method="get">
                  <table><tr class="cart_item">
                    <td class="product-remove"><a class="remove" href="/remove-cart/">Remove</a></td>
                    <td class="product-name"><a href="/product/blue-tshirt/">Blue Tshirt</a></td>
                    <td class="product-price"><span class="woocommerce-Price-amount">$10.00</span></td>
                    <td class="product-quantity"><input class="qty" name="quantity" value="%d"
                      oninput="document.querySelector('[name=update_cart]').disabled = false"></td>
                    <td class="product-subtotal"><span class="woocommerce-Price-amount">$%d.00</span></td>
                  </tr></table>
                  <button name="update_cart" disabled>Update cart</button>
                </form>
                <div class="cart_totals"><div class="cart-subtotal">
                  <span class="woocommerce-Price-amount">$%d.00</span>
                </div></div>
                """.formatted(updated ? "<div class=\"woocommerce-message\">Cart updated.</div>" : "",
                        quantity, quantity * 10, wrongCartSubtotal ? quantity * 10 + 1 : quantity * 10);
    }

    private String sortingMarkup(String selected) {
        return """
                <form class="woocommerce-ordering" method="get">
                  <select name="orderby" onchange="this.form.submit()">
                    <option value="menu_order" %s>Default sorting</option>
                    <option value="popularity">Sort by popularity</option>
                    <option value="rating">Sort by average rating</option>
                    %s
                    <option value="price" %s>Sort by price: low to high</option>
                    <option value="price-desc" %s>Sort by price: high to low</option>
                  </select>
                </form>
                """.formatted(selected.equals("menu_order") ? "selected" : "",
                        missingSortingOption ? "" : "<option value=\"date\">Sort by latest</option>",
                        selected.equals("price") ? "selected" : "",
                        selected.equals("price-desc") ? "selected" : "");
    }

    private String categoryHeading(String title) {
        return "<h1 class=\"woocommerce-products-header__title\">"
                + (wrongCategoryHeading ? "Incorrect category" : title) + "</h1>";
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
        restoreProperty("allowCartChanges", previousCartOptIn);
        restoreProperty("cartProductPath", previousProductPath);
        restoreProperty("allowCheckoutLogin", previousLoginOptIn);
        restoreProperty("allowCheckoutOrder", previousOrderOptIn);
    }

    private void restoreProperty(String key, String previous) {
        if (previous == null) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, previous);
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

    @Test
    public void sortingOptionsScenarioFailsWhenAnOptionIsMissing() {
        missingSortingOption = true;
        assertEquals(1, runCucumber("features/product-sorting.feature", "@sorting_options"));
    }

    @Test
    public void sortingScenarioFailsWhenDescendingOrderIsWrong() {
        incorrectPriceSort = true;
        assertEquals(1, runCucumber("features/product-sorting.feature", "@sorting_price"));
    }

    @Test
    public void categoryNavigationFailsForTheWrongHeading() {
        wrongCategoryHeading = true;
        assertEquals(1, runCucumber("features/product-filtering.feature", "@category_navigation"));
    }

    @Test
    public void cartSessionScenariosRunAgainstLocalFixture() {
        System.setProperty("allowCartChanges", "true");
        System.setProperty("cartProductPath", "/product/blue-tshirt/");
        assertEquals(0, runCucumber("features/cart-updates.feature", "@cart_session"));
    }

    @Test
    public void cartUpdateFailsForAnIncorrectCartSubtotal() {
        System.setProperty("allowCartChanges", "true");
        System.setProperty("cartProductPath", "/product/blue-tshirt/");
        wrongCartSubtotal = true;
        assertEquals(1, runCucumber("features/cart-updates.feature", "@AEC-32"));
    }

    @Test
    public void cartScenariosRequireOptInBeforeBrowserStartup() {
        System.clearProperty("allowCartChanges");
        System.setProperty("cartProductPath", "/product/blue-tshirt/");
        assertEquals(1, runCucumber("features/cart-updates.feature", "@AEC-28"));
    }

    @Test
    public void checkoutControlScenariosRunAgainstLocalFixture() {
        System.setProperty("allowCartChanges", "true");
        System.setProperty("cartProductPath", "/product/blue-tshirt/");
        assertEquals(0, runCucumber("features/checkout-controls.feature", "@cart_session"));
    }

    @Test
    public void checkoutScenarioRejectsUnmaskedPassword() {
        System.setProperty("allowCartChanges", "true");
        System.setProperty("cartProductPath", "/product/blue-tshirt/");
        unmaskedCheckoutPassword = true;
        assertEquals(1, runCucumber("features/checkout-controls.feature", "@AEC-79"));
    }

    @Test
    public void checkoutScenarioRejectsIncorrectReviewSubtotal() {
        System.setProperty("allowCartChanges", "true");
        System.setProperty("cartProductPath", "/product/blue-tshirt/");
        wrongCheckoutSubtotal = true;
        assertEquals(1, runCucumber("features/checkout-controls.feature", "@AEC-157"));
    }

    @Test
    public void pendingCheckoutScenariosAreRejectedEvenWithOptIns() {
        System.setProperty("allowCartChanges", "true");
        System.setProperty("allowCheckoutLogin", "true");
        System.setProperty("allowCheckoutOrder", "true");
        System.setProperty("cartProductPath", "/product/blue-tshirt/");
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> new BrowserHooks().rejectPendingScenario());
        assertEquals("Pending scenario has no approved implementation or test fixtures.", error.getMessage());
        assertEquals(1, runCucumber("features/checkout-login.feature", "@AEC-71"));
        assertEquals(1, runCucumber("features/checkout-orders.feature", "@AEC-144"));
    }

    @Test
    public void checkoutDraftStepsHaveNoMissingOrAmbiguousBindings() {
        assertEquals(0, Main.run(new String[] {
                "--glue", "com.askomdch.qa.hooks",
                "--glue", "com.askomdch.qa.steps",
                "--plugin", "summary",
                "--dry-run",
                "--tags", "@checkout_login or @checkout_order or @checkout_controls",
                "features"
        }, Thread.currentThread().getContextClassLoader()));
    }

    @Test
    public void checkoutLoginValidationRunsAgainstLocalFixture() {
        System.setProperty("allowCartChanges", "true");
        System.setProperty("allowCheckoutLogin", "true");
        System.setProperty("cartProductPath", "/product/blue-tshirt/");
        assertEquals(0, runCucumber("features/checkout-login-validation.feature", "@checkout_login"));
    }

    @Test
    public void checkoutLoginValidationRejectsAnIncorrectError() {
        System.setProperty("allowCartChanges", "true");
        System.setProperty("allowCheckoutLogin", "true");
        System.setProperty("cartProductPath", "/product/blue-tshirt/");
        wrongCheckoutLoginError = true;
        assertEquals(1, runCucumber("features/checkout-login-validation.feature", "@checkout_login"));
    }

    @Test
    public void checkoutLoginRequiresItsOwnOptIn() {
        System.setProperty("allowCartChanges", "true");
        System.clearProperty("allowCheckoutLogin");
        System.setProperty("cartProductPath", "/product/blue-tshirt/");
        assertEquals(1, runCucumber("features/checkout-login-validation.feature", "@checkout_login"));
    }

    private byte runCucumber(String features) {
        return runCucumber(features, "not @cart_session and not @pending");
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
