package com.askomdch.qa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CheckoutLoginFixtureTest {
    private static final String PRODUCT_PATH = "/product/fixture-shirt/";

    private HttpServer server;
    private final String username = "qa-checkout-" + UUID.randomUUID().toString().replace("-", "");
    private final String email = username + "@example.invalid";
    private final String password = UUID.randomUUID().toString();
    private final Map<String, String> billing = Map.of(
            "ASKOMDCH_BILLING_COUNTRY", "US",
            "ASKOMDCH_BILLING_STATE", "CA",
            "ASKOMDCH_BILLING_FIRST_NAME", "Checkout",
            "ASKOMDCH_BILLING_LAST_NAME", "Fixture",
            "ASKOMDCH_BILLING_ADDRESS", "1 Example Way",
            "ASKOMDCH_BILLING_CITY", "Example City",
            "ASKOMDCH_BILLING_POSTCODE", "00000",
            "ASKOMDCH_BILLING_PHONE", "5550100",
            "ASKOMDCH_BILLING_EMAIL", email);
    private final Map<String, String> shipping = Map.of(
            "ASKOMDCH_SHIPPING_FIRST_NAME", "Ship",
            "ASKOMDCH_SHIPPING_LAST_NAME", "Fixture",
            "ASKOMDCH_SHIPPING_COUNTRY", "US",
            "ASKOMDCH_SHIPPING_ADDRESS", "2 Example Way",
            "ASKOMDCH_SHIPPING_CITY", "Example City",
            "ASKOMDCH_SHIPPING_STATE", "CA",
            "ASKOMDCH_SHIPPING_POSTCODE", "00001");
    private final Set<String> submittedIdentifiers = ConcurrentHashMap.newKeySet();
    private volatile boolean mismatchedUnknownUsernameMessage;

    @Before
    public void startFixture() throws IOException {
        submittedIdentifiers.clear();
        mismatchedUnknownUsernameMessage = false;
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", this::handle);
        server.start();
    }

    @After
    public void stopFixture() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    public void checkoutLoginScenariosRunAgainstLocalFixture() throws Exception {
        RunResult result = runCucumber("@checkout_login and not @pending");
        assertEquals(result.output(), 0, result.exitCode());
        assertTrue("Expected all eight supported checkout-login scenarios:\n" + result.output(),
                result.output().contains("8 Scenarios (8 passed)"));
    }

    @Test
    public void unregisteredUsernameUsesTheExactAec17SampleAndMessage() throws Exception {
        mismatchedUnknownUsernameMessage = true;
        RunResult result = runCucumber("@AEC-86 and not @pending");
        assertEquals(result.output(), 1, result.exitCode());
        assertTrue("Expected the AEC-17 sample `ad` to be submitted.",
                submittedIdentifiers.contains("ad"));
        assertTrue("Expected both AEC-86 examples to run:\n" + result.output(),
                result.output().contains("2 Scenarios"));
        assertTrue("Expected exactly one message assertion to fail:\n" + result.output(),
                result.output().contains("1 failed") && result.output().contains("1 passed"));
        assertTrue("The feature must retain AEC-17's exact `ad` sample:\n" + result.output(),
                Files.readString(Path.of("features/checkout/checkout-login.feature"))
                        .contains("The username ad is not registered on this site."));
    }

    private RunResult runCucumber(String tags) throws Exception {
        List<String> command = new ArrayList<>();
        command.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
        command.add("-DbaseUrl=http://127.0.0.1:" + server.getAddress().getPort());
        command.add("-DallowCartChanges=true");
        command.add("-DallowCheckoutLogin=true");
        command.add("-DcartProductPath=" + PRODUCT_PATH);
        String chromeDriver = System.getProperty("webdriver.chrome.driver");
        if (chromeDriver != null) {
            command.add("-Dwebdriver.chrome.driver=" + chromeDriver);
        }
        command.addAll(List.of("-cp", System.getProperty("surefire.test.class.path"),
                "io.cucumber.core.cli.Main",
                "--glue", "com.askomdch.qa.hooks",
                "--glue", "com.askomdch.qa.steps",
                "--plugin", "summary",
                "--monochrome",
                "--tags", tags,
                "features/checkout/checkout-login.feature"));

        Path outputDirectory = Path.of("target", "checkout-login-fixture");
        Files.createDirectories(outputDirectory);
        Path output = Files.createTempFile(outputDirectory, "run-", ".log");
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command).redirectErrorStream(true)
                    .redirectOutput(output.toFile());
            processBuilder.environment().put("ASKOMDCH_CHECKOUT_USERNAME", username);
            processBuilder.environment().put("ASKOMDCH_CHECKOUT_EMAIL", email);
            processBuilder.environment().put("ASKOMDCH_CHECKOUT_PASSWORD", password);
            processBuilder.environment().putAll(billing);
            processBuilder.environment().putAll(shipping);
            processBuilder.environment().remove("ASKOMDCH_SHIPPING_NOTES");
            Process process = processBuilder.start();
            if (!process.waitFor(120, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                process.waitFor();
                throw new IllegalStateException("Local checkout-login fixture Cucumber process timed out.");
            }
            String result = Files.readString(output)
                    .replace(password, "[redacted]")
                    .replace(email, "[redacted-email]");
            return new RunResult(process.exitValue(), result);
        } finally {
            Files.deleteIfExists(output);
        }
    }

    private void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if ("/product/fixture-shirt/".equals(path)) {
            String notice = exchange.getRequestURI().getRawQuery() == null
                    ? "" : "<div class=\"woocommerce-message\">Added to cart</div>";
            send(exchange, 200, """
                    <!doctype html><html><body>
                    <h1 class="product_title">Fixture Shirt</h1>
                    %s
                    <form class="cart" action="/add-cart/" method="get">
                      <button name="add-to-cart" value="1">Add to cart</button>
                    </form>
                    </body></html>
                    """.formatted(notice));
        } else if ("/add-cart/".equals(path)) {
            redirect(exchange, PRODUCT_PATH + "?added=1", "fixture_cart=1; Path=/; Max-Age=3600");
        } else if ("/cart/".equals(path)) {
            send(exchange, 200, hasCookie(exchange, "fixture_cart=1")
                    ? cartMarkup()
                    : "<!doctype html><html><body><p class=\"cart-empty\">Your cart is currently empty.</p></body></html>");
        } else if ("/checkout/".equals(path)) {
            checkout(exchange);
        } else if ("/account/".equals(path)) {
            if (hasCookie(exchange, "fixture_checkout_auth=1")) {
                send(exchange, 200, "<!doctype html><html><body><div class=\"woocommerce-MyAccount-content\">"
                        + "<p>Hello " + username + "</p></div></body></html>");
            } else {
                send(exchange, 200, "<!doctype html><html><body>Not authenticated</body></html>");
            }
        } else {
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
        }
    }

    private void checkout(HttpExchange exchange) throws IOException {
        if (!hasCookie(exchange, "fixture_cart=1")) {
            redirect(exchange, "/cart/", null);
            return;
        }
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            Map<String, String> fields = parseForm(exchange.getRequestBody().readAllBytes());
            String identifier = fields.getOrDefault("username", "");
            String submittedPassword = fields.getOrDefault("password", "");
            submittedIdentifiers.add(identifier);
            String error = loginError(identifier, submittedPassword);
            if (error == null) {
                String persistent = fields.containsKey("rememberme") ? "; Max-Age=3600" : "";
                redirect(exchange, "/checkout/", "fixture_checkout_auth=1; Path=/" + persistent);
            } else {
                send(exchange, 200, checkoutMarkup(false, error));
            }
            return;
        }
        send(exchange, 200, checkoutMarkup(hasCookie(exchange, "fixture_checkout_auth=1"), null));
    }

    private String loginError(String identifier, String submittedPassword) {
        if (identifier.isBlank()) {
            return "Error: Username is required";
        }
        if (submittedPassword.isBlank()) {
            return "Error: the password field is empyt";
        }
        boolean registered = username.equals(identifier) || email.equals(identifier);
        if (registered && password.equals(submittedPassword)) {
            return null;
        }
        if (registered) {
            return "Error: the password you entered for the username " + username
                    + " is incorrect. Lost your password?";
        }
        if ("ad".equals(identifier)) {
            String sample = mismatchedUnknownUsernameMessage ? "qa-unregistered" : "ad";
            return "The username " + sample + " is not registered on this site. If you are unsure of your "
                    + "username, try your email address instead.";
        }
        if (identifier.contains("@")) {
            return "Unknown email address. Check again or try your username.";
        }
        return "The username " + identifier
                + " is not registered on this site. If you are unsure of your username, try your email address instead.";
    }

    private String checkoutMarkup(boolean authenticated, String error) {
        StringBuilder html = new StringBuilder("<!doctype html><html><body>");
        if (!authenticated) {
            html.append("""
                    <div class="woocommerce-info">Returning customer?
                      <a class="showlogin" href="#">Click here to login</a>
                    </div>
                    """);
            if (error != null) {
                String displayedError = error.replace("Lost your password?",
                        "<a href=\"/account/lost-password/\">Lost your password?</a>");
                html.append("<ul class=\"woocommerce-error\"><li>").append(displayedError).append("</li></ul>");
            }
            html.append("""
                    <form class="login" action="/checkout/" method="post" style="display:%s">
                      <input name="username">
                      <input name="password" type="password">
                      <label><input name="rememberme" type="checkbox">Remember me</label>
                      <button name="login" value="login">LOGIN</button>
                      <p class="lost_password"><a href="/account/lost-password/">Lost your password?</a></p>
                    </form>
                    <script>
                    document.querySelector('.showlogin').addEventListener('click', function(event) {
                      event.preventDefault();
                      document.querySelector('form.login').style.display = 'block';
                    });
                    </script>
                    """.formatted(error == null ? "none" : "block"));
        }
        html.append("<form class=\"checkout\" action=\"/checkout/\" method=\"post\">");
        Map<String, String> fields = new LinkedHashMap<>();
        billing.forEach((name, value) -> fields.put(checkoutFieldName(name), value));
        shipping.forEach((name, value) -> fields.put(checkoutFieldName(name), value));
        for (Map.Entry<String, String> field : fields.entrySet()) {
            html.append("<input id=\"").append(field.getKey()).append("\" name=\"")
                    .append(field.getKey()).append("\" value=\"")
                    .append(authenticated ? escapeHtml(field.getValue()) : "").append("\">");
        }
        return html.append("</form></body></html>").toString();
    }

    private String cartMarkup() {
        return """
                <!doctype html><html><body>
                <form class="woocommerce-cart-form" action="/cart/" method="post">
                  <table><tbody><tr class="cart_item">
                    <td class="product-name"><a href="%s">Fixture Shirt</a></td>
                    <td class="product-quantity"><input class="qty" value="1"></td>
                    <td class="product-price"><span class="woocommerce-Price-amount">$10.00</span></td>
                    <td class="product-subtotal"><span class="woocommerce-Price-amount">$10.00</span></td>
                  </tr></tbody></table>
                </form>
                </body></html>
                """.formatted(PRODUCT_PATH);
    }

    private static Map<String, String> parseForm(byte[] body) {
        return Arrays.stream(new String(body, StandardCharsets.UTF_8).split("&"))
                .map(part -> part.split("=", 2))
                .collect(Collectors.toMap(
                        part -> URLDecoder.decode(part[0], StandardCharsets.UTF_8),
                        part -> part.length == 2 ? URLDecoder.decode(part[1], StandardCharsets.UTF_8) : "",
                        (first, second) -> second));
    }

    private static String checkoutFieldName(String environmentName) {
        String name = environmentName.replaceFirst("^ASKOMDCH_", "").toLowerCase();
        return name.replaceFirst("^billing_address$", "billing_address_1")
                .replaceFirst("^shipping_address$", "shipping_address_1");
    }

    private static String escapeHtml(String value) {
        return value.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static boolean hasCookie(HttpExchange exchange, String cookie) {
        String cookies = exchange.getRequestHeaders().getFirst("Cookie");
        return cookies != null && cookies.contains(cookie);
    }

    private static void redirect(HttpExchange exchange, String location, String cookie) throws IOException {
        if (cookie != null) {
            exchange.getResponseHeaders().add("Set-Cookie", cookie);
        }
        exchange.getResponseHeaders().set("Location", location);
        exchange.sendResponseHeaders(303, -1);
        exchange.close();
    }

    private static void send(HttpExchange exchange, int status, String body) throws IOException {
        byte[] response = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(status, response.length);
        try (var output = exchange.getResponseBody()) {
            output.write(response);
        }
    }

    private record RunResult(int exitCode, String output) {
    }
}
