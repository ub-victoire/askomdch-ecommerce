package com.askomdch.qa.config;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TestMailboxClient {
    private static final Pattern COUNT = Pattern.compile("\"count\"\\s*:\\s*(\\d+)");
    private static final Pattern RESET_URL = Pattern.compile("\"resetUrl\"\\s*:\\s*\"([^\"]+)\"");
    private final URI endpoint;
    private final String token;
    private final HttpClient client;

    public TestMailboxClient() {
        String value = System.getenv("ASKOMDCH_TEST_MAILBOX_URL");
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Set ASKOMDCH_TEST_MAILBOX_URL to the authorized test-mailbox query endpoint.");
        }
        endpoint = URI.create(value);
        if (!("http".equalsIgnoreCase(endpoint.getScheme()) || "https".equalsIgnoreCase(endpoint.getScheme()))
                || endpoint.getHost() == null || endpoint.getUserInfo() != null
                || endpoint.getQuery() != null || endpoint.getFragment() != null) {
            throw new IllegalArgumentException(
                    "ASKOMDCH_TEST_MAILBOX_URL must be an HTTP(S) endpoint without credentials, query, or fragment.");
        }
        String host = endpoint.getHost().replaceAll("\\.+$", "").toLowerCase(java.util.Locale.ROOT);
        if (host.equals("askomdch.com") || host.endsWith(".askomdch.com")) {
            throw new IllegalArgumentException("The test mailbox endpoint must not use the public store domain.");
        }
        token = System.getenv("ASKOMDCH_TEST_MAILBOX_TOKEN");
        client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NEVER).build();
    }

    public int countSince(String recipient, Instant since) {
        HttpResponse<String> response = send(recipient, since);
        Matcher matcher = COUNT.matcher(response.body());
        if (!matcher.find()) {
            throw new IllegalStateException("Mailbox query response must contain a numeric count field.");
        }
        return Integer.parseInt(matcher.group(1));
    }

    public URI awaitResetLink(String recipient, Instant since, Duration timeout) {
        Instant deadline = Instant.now().plus(timeout);
        while (Instant.now().isBefore(deadline)) {
            HttpResponse<String> response = send(recipient, since);
            Matcher count = COUNT.matcher(response.body());
            if (!count.find()) {
                throw new IllegalStateException("Mailbox query response must contain a numeric count field.");
            }
            if (Integer.parseInt(count.group(1)) > 0) {
                Matcher link = RESET_URL.matcher(response.body());
                if (!link.find()) {
                    throw new IllegalStateException("The test mailbox did not return the reset link.");
                }
                URI resetUrl = URI.create(link.group(1));
                TestConfig.requireSameOrigin(resetUrl.toString());
                return resetUrl;
            }
            pause();
        }
        throw new IllegalStateException("No reset email arrived in the configured test mailbox before timeout.");
    }

    public void assertNoMessageDuring(String recipient, Instant since, Duration observationWindow) {
        Instant deadline = Instant.now().plus(observationWindow);
        do {
            if (countSince(recipient, since) != 0) {
                throw new AssertionError("A password reset email was observed for a rejected request.");
            }
            pause();
        } while (Instant.now().isBefore(deadline));
    }

    private HttpResponse<String> send(String recipient, Instant since) {
        String query = "?recipient=" + URLEncoder.encode(recipient, StandardCharsets.UTF_8)
                + "&since=" + since.toEpochMilli();
        HttpRequest.Builder request = HttpRequest.newBuilder(
                        URI.create(endpoint.toString() + query))
                .timeout(Duration.ofSeconds(10)).GET().header("Accept", "application/json");
        if (token != null && !token.isBlank()) {
            request.header("Authorization", "Bearer " + token);
        }
        try {
            HttpResponse<String> response = client.send(request.build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Test mailbox query returned HTTP " + response.statusCode() + ".");
            }
            return response;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Test mailbox query was interrupted.", exception);
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Test mailbox query failed.", exception);
        }
    }

    private void pause() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Test mailbox polling was interrupted.", exception);
        }
    }
}
