package com.askomdch.qa.config;

import java.net.URI;
import java.net.URISyntaxException;

public final class TestConfig {
    private TestConfig() {
    }

    public static URI baseUrl() {
        String value = System.getProperty("baseUrl");
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Set -DbaseUrl to an explicitly authorized test environment.");
        }

        try {
            URI uri = new URI(value);
            if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                    || uri.getHost() == null || uri.getUserInfo() != null
                    || uri.getQuery() != null || uri.getFragment() != null
                    || (uri.getPath() != null && !uri.getPath().isEmpty() && !"/".equals(uri.getPath()))) {
                throw new IllegalArgumentException(
                        "baseUrl must be an HTTP(S) origin without credentials, path, query, or fragment.");
            }
            return uri;
        } catch (URISyntaxException exception) {
            throw new IllegalArgumentException("baseUrl is not a valid URI.", exception);
        }
    }

    public static void requireCartChangesAllowed() {
        if (!Boolean.getBoolean("allowCartChanges")) {
            throw new IllegalStateException("Cart scenarios require -DallowCartChanges=true.");
        }
        requireNonProductionOrigin();
    }

    public static void requireAccountLoginAllowed() {
        if (!Boolean.getBoolean("allowAccountLogin")) {
            throw new IllegalStateException("Account login scenarios require -DallowAccountLogin=true.");
        }
        requireNonProductionOrigin();
    }

    public static void requireAccountRegistrationAllowed() {
        if (!Boolean.getBoolean("allowAccountRegistration")) {
            throw new IllegalStateException(
                    "Account registration scenarios require -DallowAccountRegistration=true.");
        }
        requireNonProductionOrigin();
    }

    public static void requireAccountRecoveryAllowed() {
        if (!Boolean.getBoolean("allowAccountRecovery")) {
            throw new IllegalStateException(
                    "Account recovery scenarios require -DallowAccountRecovery=true.");
        }
        requireNonProductionOrigin();
    }

    private static void requireNonProductionOrigin() {
        URI origin = baseUrl();
        String host = origin.getHost().replaceAll("\\.+$", "").toLowerCase(java.util.Locale.ROOT);
        if (host.equals("askomdch.com") || host.endsWith(".askomdch.com")) {
            throw new IllegalStateException("State-changing scenarios require a non-production environment.");
        }
    }

    public static void requireCheckoutLoginAllowed() {
        requireCartChangesAllowed();
        if (!Boolean.getBoolean("allowCheckoutLogin")) {
            throw new IllegalStateException("Checkout login scenarios require -DallowCheckoutLogin=true.");
        }
    }

    public static void requireCheckoutOrderAllowed() {
        requireCartChangesAllowed();
        if (!Boolean.getBoolean("allowCheckoutOrder")) {
            throw new IllegalStateException("Checkout order scenarios require -DallowCheckoutOrder=true.");
        }
    }

    public static String requiredProperty(String name) {
        String value = System.getProperty(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Set -D" + name + " using an approved synthetic test fixture.");
        }
        return value;
    }

    public static void requireSameOrigin(String url) {
        URI expected = baseUrl();
        URI actual = URI.create(url);
        if (!expected.getScheme().equalsIgnoreCase(actual.getScheme())
                || !expected.getHost().equalsIgnoreCase(actual.getHost())
                || expected.getPort() != actual.getPort()) {
            throw new IllegalStateException("Browser action must remain on the configured origin: " + actual);
        }
    }

    public static URI cartProductUrl() {
        String path = System.getProperty("cartProductPath");
        if (path == null || !path.matches("/product/[a-zA-Z0-9_-]+/?")) {
            throw new IllegalArgumentException(
                    "Set -DcartProductPath to an approved in-stock product path, such as /product/example/.");
        }
        return baseUrl().resolve(path);
    }
}
