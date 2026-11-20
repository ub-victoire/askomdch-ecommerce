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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AccountLoginFixtureTest {
    private HttpServer server;
    private final String username = "QaTester";
    private final String email = UUID.randomUUID() + "@example.invalid";
    private final String password = UUID.randomUUID().toString();
    private volatile boolean rejectLogin;
    private volatile boolean leakPassword;

    @Before
    public void startFixture() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/account/", this::account);
        server.start();
    }

    @After
    public void stopFixture() {
        if (server != null) {
            server.stop(0);
        }
    }

    private void account(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            Map<String, String> fields = Arrays.stream(new String(
                    exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8).split("&"))
                    .map(part -> part.split("=", 2))
                    .collect(Collectors.toMap(
                            part -> URLDecoder.decode(part[0], StandardCharsets.UTF_8),
                            part -> part.length == 2 ? URLDecoder.decode(part[1], StandardCharsets.UTF_8) : "",
                            (first, second) -> second));
            boolean valid = (username.equalsIgnoreCase(fields.get("username"))
                    || email.equals(fields.get("username"))) && password.equals(fields.get("password"));
            if (valid && !rejectLogin) {
                String persistent = fields.containsKey("rememberme") ? "; Max-Age=3600" : "";
                exchange.getResponseHeaders().add("Set-Cookie", "fixture_auth=1; Path=/" + persistent);
            }
            String location = leakPassword ? "/account/?password=" + password : "/account/";
            exchange.getResponseHeaders().set("Location", location);
            exchange.sendResponseHeaders(303, -1);
            exchange.close();
            return;
        }
        String cookies = exchange.getRequestHeaders().getFirst("Cookie");
        boolean authenticated = cookies != null && cookies.contains("fixture_auth=1");
        String html = authenticated
                ? "<div class=\"woocommerce-MyAccount-content\"><p>Hello " + username + "</p></div>"
                : (rejectLogin ? "<div class=\"woocommerce-error\">Login rejected</div>" : "") + """
                  <form class="woocommerce-form-login" action="/account/" method="post">
                    <input id="username" name="username">
                    <input id="password" name="password" type="password">
                    <input id="rememberme" name="rememberme" type="checkbox">
                    <button name="login" value="login">Log in</button>
                  </form>
                  """;
        byte[] body = ("<!doctype html><html><body>" + html + "</body></html>")
                .getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, body.length);
        try (var output = exchange.getResponseBody()) {
            output.write(body);
        }
    }

    @Test
    public void accountLoginScenariosRunAgainstLocalFixture() throws Exception {
        assertEquals(0, runScenarios("@account_login and not @pending", true, null));
    }

    @Test
    public void accountLoginFailsWhenAuthenticationDoesNotSucceed() throws Exception {
        rejectLogin = true;
        assertEquals(1, runScenarios("@AEC-90", true, "TimeoutException"));
    }

    @Test
    public void passwordLeakIntoUrlIsDetected() throws Exception {
        leakPassword = true;
        assertEquals(1, runScenarios("@AEC-98", true, "The browser URL must not contain the test password."));
    }

    @Test
    public void accountLoginRequiresOptInBeforeBrowserStartup() throws Exception {
        assertEquals(1, runScenarios("@AEC-90", false, "Account login scenarios require -DallowAccountLogin=true."));
    }

    private int runScenarios(String tags, boolean allowLogin, String expectedFailure) throws Exception {
        List<String> command = new ArrayList<>();
        command.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
        command.add("-DbaseUrl=http://127.0.0.1:" + server.getAddress().getPort());
        if (allowLogin) {
            command.add("-DallowAccountLogin=true");
        }
        String chromeDriver = System.getProperty("webdriver.chrome.driver");
        if (chromeDriver != null) {
            command.add("-Dwebdriver.chrome.driver=" + chromeDriver);
        }
        command.addAll(List.of("-cp", System.getProperty("surefire.test.class.path"),
                "io.cucumber.core.cli.Main", "--glue", "com.askomdch.qa.hooks",
                "--glue", "com.askomdch.qa.steps", "--plugin", "summary", "--monochrome",
                "--tags", tags, "features/account-management/account-login-draft.feature"));
        Path output = Files.createTempFile("askomdch-account-login-fixture-", ".log");
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command).redirectErrorStream(true)
                    .redirectOutput(output.toFile());
            processBuilder.environment().put("ASKOMDCH_CHECKOUT_USERNAME", username);
            processBuilder.environment().put("ASKOMDCH_CHECKOUT_EMAIL", email);
            processBuilder.environment().put("ASKOMDCH_CHECKOUT_PASSWORD", password);
            Process process = processBuilder.start();
            if (!process.waitFor(120, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                process.waitFor();
                throw new IllegalStateException("Local account fixture Cucumber process timed out.");
            }
            int exit = process.exitValue();
            String result = Files.readString(output).replace(password, "[redacted]");
            if (expectedFailure == null) {
                assertTrue("Expected all seven selected scenarios to execute:\n" + result,
                        result.contains("7 Scenarios (7 passed)"));
            } else {
                assertTrue("Expected failure was not observed:\n" + result, result.contains(expectedFailure));
            }
            return exit;
        } finally {
            Files.deleteIfExists(output);
        }
    }
}
