package com.askomdch.qa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.askomdch.qa.hooks.BrowserHooks;
import com.askomdch.qa.pages.AccountPage;
import com.askomdch.qa.pages.RecoveryPage;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import io.cucumber.core.cli.Main;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
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
    private String previousRegistrationOptIn;
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
    private volatile boolean nativeRegistrationEmailValidation;
    private volatile boolean wrongOrderPayment;
    private volatile boolean wrongShippingAddress;
    private volatile Map<String, String> submittedShipping;
    private volatile boolean checkoutHidesLoginWithoutAuthentication;
    private final Map<String, String> registeredFixtureAccounts = new HashMap<>();
    private final Map<String, List<Long>> recoveryMessageTimes = new HashMap<>();

    @Before
    public void startFixture() throws IOException {
        previousBaseUrl = System.getProperty("baseUrl");
        previousCartOptIn = System.getProperty("allowCartChanges");
        previousProductPath = System.getProperty("cartProductPath");
        previousLoginOptIn = System.getProperty("allowCheckoutLogin");
        previousOrderOptIn = System.getProperty("allowCheckoutOrder");
        previousRegistrationOptIn = System.getProperty("allowAccountRegistration");
        registeredFixtureAccounts.clear();
        registeredFixtureAccounts.put("RecoveryUser", "OldPass1!\nrecovery@example.invalid");
        recoveryMessageTimes.clear();
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
        nativeRegistrationEmailValidation = false;
        wrongOrderPayment = false;
        wrongShippingAddress = false;
        submittedShipping = Map.of();
        checkoutHidesLoginWithoutAuthentication = false;
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/account/", this::accountFixture);
        server.createContext("/mailbox", this::mailboxFixture);
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
            } else if (path.equals("/checkout/order-received/1/")) {
                String cookies = exchange.getRequestHeaders().getFirst("Cookie");
                if (cookies == null || !cookies.contains("fixture_order=")) {
                    exchange.sendResponseHeaders(404, -1);
                    exchange.close();
                    return;
                }
                boolean cash = cookies.contains("fixture_order=cod");
                if (wrongOrderPayment) {
                    cash = !cash;
                }
                html = """
                        <ul>
                          <li class="woocommerce-order-overview__order order">Order number: <strong>1</strong></li>
                          <li class="woocommerce-order-overview__payment-method">Payment method: <strong>%s</strong></li>
                        </ul>
                        %s
                        """.formatted(cash ? "Cash on delivery" : "Direct bank transfer",
                        submittedShipping.isEmpty() ? "" : """
                          <div class="woocommerce-customer-details"><div class="woocommerce-column--shipping-address">
                            %s %s<br>%s<br>%s<br>%s<br>%s<br>%s
                          </div></div>
                          """.formatted(submittedShipping.get("shipping_first_name"),
                                wrongShippingAddress ? "WrongName" : submittedShipping.get("shipping_last_name"),
                                submittedShipping.get("shipping_address_1"), submittedShipping.get("shipping_city"),
                                "California", submittedShipping.get("shipping_postcode"), "United States"));
            } else if (path.equals("/checkout/")) {
                if (cartQuantity(exchange) != 1) {
                    exchange.getResponseHeaders().set("Location", "/cart/");
                    exchange.sendResponseHeaders(303, -1);
                    exchange.close();
                    return;
                }
                if (exchange.getRequestMethod().equals("POST")) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Map<String, String> fields = parseForm(body);
                    if (fields.containsKey("place_order")) {
                        String method = fields.get("payment_method");
                        if (!("bacs".equals(method) || "cod".equals(method))
                                || List.of("billing_first_name", "billing_last_name", "billing_country",
                                        "billing_address_1", "billing_city", "billing_state", "billing_postcode",
                                        "billing_phone", "billing_email").stream()
                                        .anyMatch(field -> fields.getOrDefault(field, "").isBlank())) {
                            exchange.sendResponseHeaders(400, -1);
                            exchange.close();
                            return;
                        }
                        submittedShipping = fields.containsKey("ship_to_different_address")
                                ? Map.copyOf(fields) : Map.of();
                        exchange.getResponseHeaders().add("Set-Cookie", "fixture_order=" + method + "; Path=/");
                        exchange.getResponseHeaders().set("Location", "/checkout/order-received/1/");
                        exchange.sendResponseHeaders(303, -1);
                        exchange.close();
                        return;
                    }
                    String identifier = fields.getOrDefault("username", "");
                    if ((identifier.equals("RecoveryUser") || identifier.equals("recovery@example.invalid"))
                            && fields.getOrDefault("password", "").equals("OldPass1!")) {
                        if (!checkoutHidesLoginWithoutAuthentication) {
                            exchange.getResponseHeaders().add("Set-Cookie", "fixture_checkout_auth=1; Path=/");
                        } else {
                            exchange.getResponseHeaders().add("Set-Cookie", "fixture_checkout_hide=1; Path=/");
                        }
                        exchange.getResponseHeaders().set("Location", "/checkout/");
                        exchange.sendResponseHeaders(303, -1);
                        exchange.close();
                        return;
                    }
                    if (!body.startsWith("username=&password=")) {
                        exchange.sendResponseHeaders(400, -1);
                        exchange.close();
                        return;
                    }
                    html = "<ul class=\"woocommerce-error\"><li>"
                            + (wrongCheckoutLoginError ? "Incorrect fixture error" : "Error: Username is required")
                            + "</li></ul>" + checkoutMarkup();
                } else {
                    String cookies = exchange.getRequestHeaders().getFirst("Cookie");
                    boolean hideLogin = cookies != null && (cookies.contains("fixture_checkout_auth=1")
                            || cookies.contains("fixture_checkout_hide=1"));
                    html = checkoutMarkup();
                    if (hideLogin) {
                        html = html.replace("<div class=\"woocommerce-info\">", "<div style=\"display:none\" class=\"woocommerce-info\">");
                    }
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
                <form class="checkout" action="/checkout/" method="post">
                <input id="billing_first_name" name="billing_first_name">
                <input id="billing_last_name" name="billing_last_name">
                <select id="billing_country" name="billing_country"><option value="">Select</option>
                  <option value="US">United States</option></select>
                <input id="billing_address_1" name="billing_address_1">
                <input id="billing_city" name="billing_city">
                <select id="billing_state" name="billing_state"><option value="">Select</option>
                  <option value="CA">California</option></select>
                <input id="billing_postcode" name="billing_postcode">
                <input id="billing_phone" name="billing_phone">
                <input id="billing_email" name="billing_email">
                <label><input id="ship-to-different-address-checkbox" name="ship_to_different_address"
                  type="checkbox" value="1"
                  onchange="document.getElementById('shipping_fields').style.display = this.checked ? 'block' : 'none'">
                  Ship to a different address?</label>
                <div id="shipping_fields" style="display:none">
                  <select id="shipping_country" name="shipping_country"><option value="">Select</option>
                    <option value="US">United States</option></select>
                  <select id="shipping_state" name="shipping_state"><option value="">Select</option>
                    <option value="CA">California</option></select>
                  <input id="shipping_first_name" name="shipping_first_name">
                  <input id="shipping_last_name" name="shipping_last_name">
                  <input id="shipping_address_1" name="shipping_address_1">
                  <input id="shipping_city" name="shipping_city">
                  <input id="shipping_postcode" name="shipping_postcode">
                </div>
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
                  <input type="radio" name="payment_method" id="payment_method_bacs" value="bacs" checked
                    onclick="document.querySelector('.payment_method_bacs').style.display='block';
                      document.querySelector('.payment_method_cod').style.display='none'">
                  <div class="payment_box payment_method_bacs">Bank transfer instructions</div>
                  <input type="radio" name="payment_method" id="payment_method_cod" value="cod"
                    onclick="document.querySelector('.payment_method_cod').style.display='block';
                      document.querySelector('.payment_method_bacs').style.display='none'">
                  <div class="payment_box payment_method_cod" style="display:none">Cash instructions</div>
                </div>
                <button id="place_order" name="place_order" value="1">Place order</button>
                </form>
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
        restoreProperty("allowAccountRegistration", previousRegistrationOptIn);
    }

    private void accountFixture(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/account/lost-password/") && "POST".equals(exchange.getRequestMethod())
                && exchange.getRequestURI().getRawQuery() == null) {
            recoveryRequest(exchange);
            return;
        }
        if (path.equals("/account/lost-password/") && "GET".equals(exchange.getRequestMethod())
                && exchange.getRequestURI().getRawQuery() != null
                && exchange.getRequestURI().getRawQuery().contains("key=fixture")) {
            writeHtml(exchange, """
                    <form class="woocommerce-ResetPassword" action="%s" method="post">
                      <input id="password_1" name="password_1" type="password">
                      <input id="password_2" name="password_2" type="password">
                      <button type="submit">Save</button>
                    </form>
                    """.formatted(exchange.getRequestURI()));
            return;
        }
        if (path.equals("/account/lost-password/") && "POST".equals(exchange.getRequestMethod())
                && exchange.getRequestURI().getRawQuery() != null) {
            resetPassword(exchange);
            return;
        }
        if (path.equals("/account/customer-logout/")) {
            exchange.getResponseHeaders().add("Set-Cookie", "fixture_account=; Path=/; Max-Age=0");
            writeHtml(exchange, accountMarkup("", false));
            return;
        }
        if ("POST".equals(exchange.getRequestMethod())) {
            Map<String, String> fields = parseForm(new String(
                    exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            if (fields.containsKey("register")) {
                registerFixtureAccount(exchange, fields);
                return;
            }
            if (fields.containsKey("login")) {
                loginFixtureAccount(exchange, fields);
                return;
            }
        }
        boolean authenticated = hasAccountCookie(exchange);
        String cookies = exchange.getRequestHeaders().getFirst("Cookie");
        boolean checkoutAuthenticated = cookies != null && cookies.contains("fixture_checkout_auth=1");
        authenticated = authenticated || checkoutAuthenticated;
        String html = path.equals("/account/lost-password/")
                ? """
                  <form class="lost_reset_password" action="/account/lost-password/" method="post">
                    %s
                    <p><label for="user_login">Username or email</label>
                    <input id="user_login" name="user_login"></p>
                    <button type="submit">Reset password</button>
                  </form>
                  """.formatted(showRecoveryInstruction ? "<p>Recovery instructions</p>" : "")
                : checkoutAuthenticated ? accountMarkup("", true, "RecoveryUser") : accountMarkup("", authenticated);
        writeHtml(exchange, html);
    }

    private void recoveryRequest(HttpExchange exchange) throws IOException {
        Map<String, String> fields = parseForm(new String(
                exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
        String identifier = fields.getOrDefault("user_login", "");
        Map.Entry<String, String> account = registeredFixtureAccounts.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(identifier)
                        || entry.getValue().endsWith("\n" + identifier))
                .findFirst().orElse(null);
        String error = null;
        if (identifier.isBlank()) {
            error = "Enter a username or email address.";
        } else if (account == null) {
            error = identifier.contains("@")
                    ? "There is no account with that username or email address."
                    : "Invalid username or email.";
        }
        if (error != null) {
            writeHtml(exchange, recoveryMarkup(error, false));
            return;
        }
        String email = account.getValue().substring(account.getValue().indexOf('\n') + 1);
        recoveryMessageTimes.computeIfAbsent(email, ignored -> new ArrayList<>()).add(System.currentTimeMillis());
        writeHtml(exchange, recoveryMarkup("Password reset email has been sent.", true));
    }

    private String recoveryMarkup(String message, boolean success) {
        String block = success ? "<div class=\"woocommerce-message\">" + message + "</div>"
                : "<ul class=\"woocommerce-error\"><li>" + message + "</li></ul>";
        return """
                %s
                <form class="lost_reset_password" action="/account/lost-password/" method="post">
                  <p>Enter your username or email address.</p>
                  <label for="user_login">Username or email</label>
                  <input id="user_login" name="user_login">
                  <button type="submit">Reset password</button>
                </form>
                """.formatted(block);
    }

    private void resetPassword(HttpExchange exchange) throws IOException {
        Map<String, String> query = parseForm(exchange.getRequestURI().getRawQuery());
        Map<String, String> fields = parseForm(new String(
                exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
        String username = query.get("user");
        String existing = registeredFixtureAccounts.get(username);
        if (existing != null && fields.getOrDefault("password_1", "")
                .equals(fields.getOrDefault("password_2", ""))) {
            String email = existing.substring(existing.indexOf('\n') + 1);
            registeredFixtureAccounts.put(username, fields.get("password_1") + "\n" + email);
        }
        exchange.getResponseHeaders().set("Location", "/account/");
        exchange.sendResponseHeaders(303, -1);
        exchange.close();
    }

    private void mailboxFixture(HttpExchange exchange) throws IOException {
        Map<String, String> query = parseForm(exchange.getRequestURI().getRawQuery());
        String recipient = query.getOrDefault("recipient", "");
        long since = Long.parseLong(query.getOrDefault("since", "0"));
        List<Long> messages = recoveryMessageTimes.getOrDefault(recipient, List.of());
        long count = messages.stream().filter(received -> received >= since).count();
        String response = "{\"count\":" + count;
        if (count > 0) {
            String username = registeredFixtureAccounts.entrySet().stream()
                    .filter(entry -> entry.getValue().endsWith("\n" + recipient))
                    .map(Map.Entry::getKey).findFirst().orElseThrow();
            response += ",\"resetUrl\":\"http://127.0.0.1:" + server.getAddress().getPort()
                    + "/account/lost-password/?key=fixture&user=" + username + "\"";
        }
        byte[] body = (response + "}").getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(200, body.length);
        try (var output = exchange.getResponseBody()) {
            output.write(body);
        }
    }

    private void registerFixtureAccount(HttpExchange exchange, Map<String, String> fields) throws IOException {
        String username = fields.getOrDefault("username", "");
        String email = fields.getOrDefault("email", "");
        String password = fields.getOrDefault("password", "");
        String error = null;
        if ("ExistingUser".equalsIgnoreCase(username)) {
            error = "Error: An account is already registered with that username. Please choose another.";
        } else if ("existing@example.invalid".equalsIgnoreCase(email)) {
            error = "Error: An account is already registered with your email address. Please log in.";
        } else if (username.isBlank()) {
            error = "Please enter a valid account username";
        } else if (email.isBlank()) {
            error = "Please provide a valid email address";
        } else if (!email.contains("@")) {
            error = "Please include @ in the email address";
        } else if (password.isBlank()) {
            error = "Please enter an account password";
        }
        if (error != null) {
            writeHtml(exchange, accountMarkup(error, false));
            return;
        }
        registeredFixtureAccounts.put(username, password + "\n" + email);
        exchange.getResponseHeaders().add("Set-Cookie", "fixture_account=1; Path=/");
        writeHtml(exchange, accountMarkup("", true, username));
    }

    private void loginFixtureAccount(HttpExchange exchange, Map<String, String> fields) throws IOException {
        String identifier = fields.getOrDefault("username", "");
        String password = fields.getOrDefault("password", "");
        String matchedUsername = registeredFixtureAccounts.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(identifier)
                        || entry.getValue().endsWith("\n" + identifier))
                .filter(entry -> entry.getValue().startsWith(password + "\n"))
                .map(Map.Entry::getKey).findFirst().orElse(null);
        if (matchedUsername == null) {
            writeHtml(exchange, accountMarkup("Login failed", false));
            return;
        }
        exchange.getResponseHeaders().add("Set-Cookie", "fixture_account=1; Path=/");
        writeHtml(exchange, accountMarkup("", true, matchedUsername));
    }

    private String accountMarkup(String error, boolean authenticated) {
        return accountMarkup(error, authenticated, "FixtureUser");
    }

    private String accountMarkup(String error, boolean authenticated, String username) {
        if (authenticated) {
            return """
                    <nav class="woocommerce-MyAccount-navigation">
                      <ul>
                        <li class="woocommerce-MyAccount-navigation-link"><a href="/account/">Dashboard</a></li>
                        <li class="woocommerce-MyAccount-navigation-link"><a href="/account/orders/">Orders</a></li>
                        <li class="woocommerce-MyAccount-navigation-link"><a href="/account/downloads/">Downloads</a></li>
                        <li class="woocommerce-MyAccount-navigation-link"><a href="/account/edit-address/">Addresses</a></li>
                        <li class="woocommerce-MyAccount-navigation-link"><a href="/account/edit-account/">Account details</a></li>
                        <li class="woocommerce-MyAccount-navigation-link woocommerce-MyAccount-navigation-link--customer-logout">
                          <a href="/account/customer-logout/">Logout</a>
                        </li>
                      </ul>
                    </nav>
                    <div class="woocommerce-MyAccount-content"><p>Hello %s</p></div>
                    """.formatted(username);
        }
        return """
                %s
                <form class="woocommerce-form-login" action="/account/" method="post">
                  <input id="username" name="username">
                  <input id="password" name="password" type="password">
                  <label class="woocommerce-form-login__rememberme">
                    <input type="checkbox" id="rememberme" name="rememberme">Remember me
                  </label>
                  <button name="login" value="login">Log in</button>
                </form>
                <form class="woocommerce-form-register" action="/account/" method="post">
                  <input id="reg_username" name="username">
                  <input id="reg_email" name="email" type="%s">
                  <input id="reg_password" name="password" type="password">
                  <button name="register" value="register">Register</button>
                </form>
                """.formatted(error.isBlank() ? "" : "<ul class=\"woocommerce-error\"><li>" + error + "</li></ul>",
                nativeRegistrationEmailValidation ? "email" : "text");
    }

    private Map<String, String> parseForm(String body) {
        Map<String, String> values = new HashMap<>();
        Arrays.stream(body.split("&")).filter(part -> !part.isBlank()).forEach(part -> {
            String[] pair = part.split("=", 2);
            values.put(URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                    pair.length == 2 ? URLDecoder.decode(pair[1], StandardCharsets.UTF_8) : "");
        });
        return values;
    }

    private boolean hasAccountCookie(HttpExchange exchange) {
        String cookie = exchange.getRequestHeaders().getFirst("Cookie");
        return cookie != null && cookie.contains("fixture_account=1");
    }

    private void writeHtml(HttpExchange exchange, String html) throws IOException {
        byte[] body = ("<!doctype html><html><body>" + html + "</body></html>")
                .getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, body.length);
        try (var output = exchange.getResponseBody()) {
            output.write(body);
        }
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
    public void registrationReportsBrowserValidationWithoutWaitingForServerError() {
        nativeRegistrationEmailValidation = true;
        System.setProperty("allowAccountRegistration", "true");
        BrowserHooks browser = new BrowserHooks();
        try {
            browser.startBrowser();
            browser.driver().get(System.getProperty("baseUrl") + "/account/");
            AccountPage page = new AccountPage(browser.driver());
            page.enterRegistration("TestUser", "invalid-email-without-at-sign", "ValidPass1!");
            page.submitRegistration();
            org.junit.Assert.assertFalse(page.registrationValidationMessage().isBlank());
            org.junit.Assert.assertTrue(page.registrationFormVisible());
            assertEquals(1, registeredFixtureAccounts.size());
        } finally {
            browser.stopBrowser();
        }
    }

    @Test
    public void searchScenarioFailsWhenARequiredProductIsMissing() {
        wrongSearchResults = true;
        assertEquals(1, runCucumber("features/browsing-products/product-browsing.feature:15", "@AEC-18"));
    }

    @Test
    public void productScenarioFailsForAnIncorrectDetailTitle() {
        wrongProductTitle = true;
        assertEquals(1, runCucumber("features/browsing-products/product-browsing.feature:21", "@AEC-110"));
    }

    @Test
    public void emptyCartScenarioFailsWhenTotalsAreVisible() {
        showCartContents = true;
        assertEquals(1, runCucumber("features/cart-management/cart.feature", "@AEC-112"));
    }

    @Test
    public void sortingOptionsScenarioFailsWhenAnOptionIsMissing() {
        missingSortingOption = true;
        assertEquals(1, runCucumber("features/browsing-products/product-sorting.feature", "@sorting_options"));
    }

    @Test
    public void sortingScenarioFailsWhenDescendingOrderIsWrong() {
        incorrectPriceSort = true;
        assertEquals(1, runCucumber("features/browsing-products/product-sorting.feature", "@sorting_price"));
    }

    @Test
    public void categoryNavigationFailsForTheWrongHeading() {
        wrongCategoryHeading = true;
        assertEquals(1, runCucumber("features/browsing-products/product-filtering.feature", "@category_navigation"));
    }

    @Test
    public void cartSessionScenariosRunAgainstLocalFixture() {
        System.setProperty("allowCartChanges", "true");
        System.setProperty("cartProductPath", "/product/blue-tshirt/");
        assertEquals(0, runCucumber("features/cart-management/cart-updates.feature", "@cart_session"));
    }

    @Test
    public void cartUpdateFailsForAnIncorrectCartSubtotal() {
        System.setProperty("allowCartChanges", "true");
        System.setProperty("cartProductPath", "/product/blue-tshirt/");
        wrongCartSubtotal = true;
        assertEquals(1, runCucumber("features/cart-management/cart-updates.feature", "@AEC-32"));
    }

    @Test
    public void cartScenariosRequireOptInBeforeBrowserStartup() {
        System.clearProperty("allowCartChanges");
        System.setProperty("cartProductPath", "/product/blue-tshirt/");
        assertEquals(1, runCucumber("features/cart-management/cart-updates.feature", "@AEC-28"));
    }

    @Test
    public void checkoutControlScenariosRunAgainstLocalFixture() {
        System.setProperty("allowCartChanges", "true");
        System.setProperty("cartProductPath", "/product/blue-tshirt/");
        assertEquals(0, runCucumber("features/checkout/checkout-controls.feature", "@cart_session"));
    }

    @Test
    public void checkoutScenarioRejectsUnmaskedPassword() {
        System.setProperty("allowCartChanges", "true");
        System.setProperty("cartProductPath", "/product/blue-tshirt/");
        unmaskedCheckoutPassword = true;
        assertEquals(1, runCucumber("features/checkout/checkout-controls.feature", "@AEC-79"));
    }

    @Test
    public void checkoutScenarioRejectsIncorrectReviewSubtotal() {
        System.setProperty("allowCartChanges", "true");
        System.setProperty("cartProductPath", "/product/blue-tshirt/");
        wrongCheckoutSubtotal = true;
        assertEquals(1, runCucumber("features/checkout/checkout-controls.feature", "@AEC-157"));
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
        assertEquals(1, runCucumber("features/checkout/checkout-orders.feature", "@AEC-117"));
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
    public void checkoutAuthenticationRunsAgainstLocalFixture() throws Exception {
        runCheckoutAuthentication(0, "2 passed");
    }

    @Test
    public void hiddenLoginFormDoesNotProveAuthentication() throws Exception {
        checkoutHidesLoginWithoutAuthentication = true;
        runCheckoutAuthentication(1, "TimeoutException");
    }

    private void runCheckoutAuthentication(int expectedExit, String expectedOutput) throws Exception {
        List<String> command = new ArrayList<>();
        command.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
        command.add("-DbaseUrl=http://127.0.0.1:" + server.getAddress().getPort());
        command.add("-DallowCartChanges=true");
        command.add("-DallowCheckoutLogin=true");
        command.add("-DcartProductPath=/product/blue-tshirt/");
        String chromeDriver = System.getProperty("webdriver.chrome.driver");
        if (chromeDriver != null) {
            command.add("-Dwebdriver.chrome.driver=" + chromeDriver);
        }
        command.addAll(List.of("-cp", System.getProperty("surefire.test.class.path"),
                "io.cucumber.core.cli.Main", "--monochrome", "--glue", "com.askomdch.qa.hooks",
                "--glue", "com.askomdch.qa.steps", "--plugin", "summary",
                "features/checkout/checkout-authentication.feature"));
        Path output = Files.createTempFile("askomdch-checkout-auth-", ".log");
        try {
            ProcessBuilder builder = new ProcessBuilder(command).redirectErrorStream(true)
                    .redirectOutput(output.toFile());
            builder.environment().put("ASKOMDCH_CHECKOUT_USERNAME", "RecoveryUser");
            builder.environment().put("ASKOMDCH_CHECKOUT_EMAIL", "recovery@example.invalid");
            builder.environment().put("ASKOMDCH_CHECKOUT_PASSWORD", "OldPass1!");
            Process process = builder.start();
            if (!process.waitFor(120, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                process.waitFor();
                throw new IllegalStateException("Local checkout authentication process timed out.");
            }
            String result = Files.readString(output).replace("OldPass1!", "[redacted]");
            assertEquals(result, expectedExit, process.exitValue());
            org.junit.Assert.assertTrue("Unexpected checkout authentication result:\n" + result,
                    result.contains(expectedOutput));
        } finally {
            Files.deleteIfExists(output);
        }
    }

    @Test
    public void checkoutPaymentOrdersRunAgainstLocalFixture() throws Exception {
        runCheckoutOrderScenarios("checkout-order-payments.feature", 0, "2 Scenarios", "2 passed");
    }

    @Test
    public void checkoutPaymentOrdersRejectWrongRecordedMethod() throws Exception {
        wrongOrderPayment = true;
        runCheckoutOrderScenarios("checkout-order-payments.feature", 1, "2 Scenarios", "2 failed");
    }

    @Test
    public void checkoutShippingOrderRunsAgainstLocalFixture() throws Exception {
        runCheckoutOrderScenarios("checkout-orders.feature", 0, "1 Scenarios", "1 passed");
    }

    @Test
    public void checkoutShippingOrderRejectsIncorrectLastName() throws Exception {
        wrongShippingAddress = true;
        runCheckoutOrderScenarios("checkout-orders.feature", 1, "1 Scenarios", "1 failed");
    }

    private void runCheckoutOrderScenarios(String feature, int expectedExit,
            String expectedCount, String expectedSummary) throws Exception {
        List<String> command = new ArrayList<>();
        command.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
        command.add("-DbaseUrl=http://127.0.0.1:" + server.getAddress().getPort());
        command.add("-DallowCartChanges=true");
        command.add("-DallowCheckoutOrder=true");
        command.add("-DcartProductPath=/product/blue-tshirt/");
        String chromeDriver = System.getProperty("webdriver.chrome.driver");
        if (chromeDriver != null) {
            command.add("-Dwebdriver.chrome.driver=" + chromeDriver);
        }
        command.addAll(List.of("-cp", System.getProperty("surefire.test.class.path"),
                "io.cucumber.core.cli.Main", "--monochrome", "--glue", "com.askomdch.qa.hooks",
                "--glue", "com.askomdch.qa.steps", "--plugin", "summary",
                "--tags", "not @pending", "features/checkout/" + feature));
        Path output = Files.createTempFile("askomdch-order-fixture-", ".log");
        try {
            ProcessBuilder builder = new ProcessBuilder(command).redirectErrorStream(true)
                    .redirectOutput(output.toFile());
            Map<String, String> environment = builder.environment();
            environment.put("ASKOMDCH_BILLING_FIRST_NAME", "Test");
            environment.put("ASKOMDCH_BILLING_LAST_NAME", "Customer");
            environment.put("ASKOMDCH_BILLING_COUNTRY", "US");
            environment.put("ASKOMDCH_BILLING_ADDRESS", "1 Example Street");
            environment.put("ASKOMDCH_BILLING_CITY", "Example");
            environment.put("ASKOMDCH_BILLING_STATE", "CA");
            environment.put("ASKOMDCH_BILLING_POSTCODE", "00000");
            environment.put("ASKOMDCH_BILLING_PHONE", "0000000000");
            environment.put("ASKOMDCH_BILLING_EMAIL", "order@example.invalid");
            environment.put("ASKOMDCH_SHIPPING_FIRST_NAME", "Sample");
            environment.put("ASKOMDCH_SHIPPING_LAST_NAME", "Recipient");
            environment.put("ASKOMDCH_SHIPPING_COUNTRY", "US");
            environment.put("ASKOMDCH_SHIPPING_ADDRESS", "2 Example Avenue");
            environment.put("ASKOMDCH_SHIPPING_CITY", "Exampleville");
            environment.put("ASKOMDCH_SHIPPING_STATE", "CA");
            environment.put("ASKOMDCH_SHIPPING_POSTCODE", "11111");
            environment.remove("ASKOMDCH_SHIPPING_NOTES");
            Process process = builder.start();
            if (!process.waitFor(120, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                process.waitFor();
                throw new IllegalStateException("Local order Cucumber process timed out.");
            }
            String result = Files.readString(output);
            assertEquals(result, expectedExit, process.exitValue());
            org.junit.Assert.assertTrue("Unexpected checkout order results:\n" + result,
                    result.contains(expectedCount) && result.contains(expectedSummary));
            if (expectedExit != 0 && feature.equals("checkout-order-payments.feature")) {
                org.junit.Assert.assertTrue("Expected payment method mismatches:\n" + result,
                        result.contains("ComparisonFailure"));
            }
            if (expectedExit != 0 && feature.equals("checkout-orders.feature")) {
                org.junit.Assert.assertTrue("Expected shipping field mismatch:\n" + result,
                        result.contains("Shipping address is missing an expected field"));
            }
        } finally {
            Files.deleteIfExists(output);
        }
    }

    @Test
    public void accountRegistrationScenariosRunAgainstLocalFixture() throws Exception {
        List<String> command = new java.util.ArrayList<>();
        command.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
        command.add("-DbaseUrl=http://127.0.0.1:" + server.getAddress().getPort());
        command.add("-DallowAccountRegistration=true");
        command.add("-DallowAccountLogin=true");
        String chromeDriver = System.getProperty("webdriver.chrome.driver");
        if (chromeDriver != null) {
            command.add("-Dwebdriver.chrome.driver=" + chromeDriver);
        }

        command.addAll(List.of("-cp", System.getProperty("surefire.test.class.path"),
            "io.cucumber.core.cli.Main", "--monochrome", "--glue", "com.askomdch.qa.hooks",
                "--glue", "com.askomdch.qa.steps", "--plugin", "summary",
                "--tags", "@account_registration and not @pending",
                "features/account-management/registration-draft.feature"));
        Path output = Files.createTempFile("askomdch-registration-fixture-", ".log");
        try {
            ProcessBuilder builder = new ProcessBuilder(command).redirectErrorStream(true)
                    .redirectOutput(output.toFile());
            builder.environment().put("ASKOMDCH_REGISTRATION_USERNAME_TEMPLATE", "{letters}");
            builder.environment().put("ASKOMDCH_REGISTRATION_EMAIL_TEMPLATE", "qa-{uuid}@example.invalid");
            builder.environment().put("ASKOMDCH_REGISTRATION_PASSWORD_VALID", "ValidPass1!");
            builder.environment().put("ASKOMDCH_REGISTRATION_EXISTING_USERNAME", "ExistingUser");
            builder.environment().put("ASKOMDCH_REGISTRATION_EXISTING_EMAIL", "existing@example.invalid");
            Process process = builder.start();
            if (!process.waitFor(120, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                process.waitFor();
                throw new IllegalStateException("Local registration Cucumber process timed out.");
            }
            String result = Files.readString(output).replace("ValidPass1!", "[redacted]");
            assertEquals(result, 0, process.exitValue());
            org.junit.Assert.assertTrue("Expected all non-clarification registration scenarios:\n" + result,
                    result.contains("11 Scenarios") && result.contains("11 passed"));
        } finally {
            Files.deleteIfExists(output);
        }
    }

    @Test
    public void accountRecoveryScenariosRunAgainstLocalFixtureAndMailbox() throws Exception {
        List<String> command = new java.util.ArrayList<>();
        command.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
        command.add("-DbaseUrl=http://127.0.0.1:" + server.getAddress().getPort());
        command.add("-DallowAccountRecovery=true");
        command.add("-DallowAccountLogin=true");
        String chromeDriver = System.getProperty("webdriver.chrome.driver");
        if (chromeDriver != null) {
            command.add("-Dwebdriver.chrome.driver=" + chromeDriver);
        }
        command.addAll(List.of("-cp", System.getProperty("surefire.test.class.path"),
                "io.cucumber.core.cli.Main", "--monochrome", "--glue", "com.askomdch.qa.hooks",
                "--glue", "com.askomdch.qa.steps", "--plugin", "summary",
                "--tags", "@account_recovery and not @pending",
                "features/account-management/recovery-draft.feature"));
        Path output = Files.createTempFile("askomdch-recovery-fixture-", ".log");
        try {
            ProcessBuilder builder = new ProcessBuilder(command).redirectErrorStream(true)
                    .redirectOutput(output.toFile());
            builder.environment().put("ASKOMDCH_RECOVERY_USERNAME", "RecoveryUser");
            builder.environment().put("ASKOMDCH_RECOVERY_EMAIL", "recovery@example.invalid");
            builder.environment().put("ASKOMDCH_RECOVERY_OLD_PASSWORD", "OldPass1!");
            builder.environment().put("ASKOMDCH_RECOVERY_NEW_PASSWORD", "NewPass2@");
            builder.environment().put("ASKOMDCH_TEST_MAILBOX_URL",
                    "http://127.0.0.1:" + server.getAddress().getPort() + "/mailbox");
            builder.environment().put("ASKOMDCH_TEST_MAILBOX_NEGATIVE_WINDOW_SECONDS", "1");
            Process process = builder.start();
            if (!process.waitFor(120, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                process.waitFor();
                throw new IllegalStateException("Local recovery Cucumber process timed out.");
            }
            String result = Files.readString(output).replace("OldPass1!", "[redacted]")
                    .replace("NewPass2@", "[redacted]");
            assertEquals(result, 0, process.exitValue());
            org.junit.Assert.assertTrue("Expected all recovery scenarios to pass:\n" + result,
                    result.contains("6 Scenarios") && result.contains("6 passed"));
        } finally {
            Files.deleteIfExists(output);
        }
    }

    @Test
    public void checkoutLoginValidationRunsAgainstLocalFixture() {
        System.setProperty("allowCartChanges", "true");
        System.setProperty("allowCheckoutLogin", "true");
        System.setProperty("cartProductPath", "/product/blue-tshirt/");
        assertEquals(0, runCucumber("features/checkout/checkout-login-validation.feature", "@checkout_login"));
    }

    @Test
    public void checkoutLoginValidationRejectsAnIncorrectError() {
        System.setProperty("allowCartChanges", "true");
        System.setProperty("allowCheckoutLogin", "true");
        System.setProperty("cartProductPath", "/product/blue-tshirt/");
        wrongCheckoutLoginError = true;
        assertEquals(1, runCucumber("features/checkout/checkout-login-validation.feature", "@checkout_login"));
    }

    @Test
    public void checkoutLoginRequiresItsOwnOptIn() {
        System.setProperty("allowCartChanges", "true");
        System.clearProperty("allowCheckoutLogin");
        System.setProperty("cartProductPath", "/product/blue-tshirt/");
        assertEquals(1, runCucumber("features/checkout/checkout-login-validation.feature", "@checkout_login"));
    }

    private byte runCucumber(String features) {
        return runCucumber(features,
                "not @cart_session and not @account_login and not @account_registration"
                        + " and not @account_recovery and not @pending");
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
