package com.askomdch.qa.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestConfigTest {
    private String previous;

    @Before
    public void saveProperty() {
        previous = System.getProperty("baseUrl");
    }

    @After
    public void restoreProperty() {
        if (previous == null) {
            System.clearProperty("baseUrl");
        } else {
            System.setProperty("baseUrl", previous);
        }
    }

    @Test
    public void requiresExplicitOrigin() {
        System.clearProperty("baseUrl");
        assertThrows(IllegalArgumentException.class, TestConfig::baseUrl);
    }

    @Test
    public void rejectsCredentialsAndPaths() {
        for (String value : new String[] {
                "https://user:password@example.invalid",
                "https://example.invalid/account/",
                "file:///tmp/site",
                "https://example.invalid/?token=value"
        }) {
            System.setProperty("baseUrl", value);
            assertThrows(value, IllegalArgumentException.class, TestConfig::baseUrl);
        }
    }

    @Test
    public void acceptsHttpOrigin() {
        System.setProperty("baseUrl", "http://127.0.0.1:8080");
        assertEquals("http://127.0.0.1:8080/account/",
                TestConfig.baseUrl().resolve("/account/").toString());
    }
}
