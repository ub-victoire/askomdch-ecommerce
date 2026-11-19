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
}
