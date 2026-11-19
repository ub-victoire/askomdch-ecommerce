package com.askomdch.qa.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestConfigTest {
    private String previous;
    private String previousCartOptIn;
    private String previousProductPath;
    private String previousLoginOptIn;
    private String previousOrderOptIn;

    @Before
    public void saveProperty() {
        previous = System.getProperty("baseUrl");
        previousCartOptIn = System.getProperty("allowCartChanges");
        previousProductPath = System.getProperty("cartProductPath");
        previousLoginOptIn = System.getProperty("allowCheckoutLogin");
        previousOrderOptIn = System.getProperty("allowCheckoutOrder");
    }

    @After
    public void restoreProperty() {
        if (previous == null) {
            System.clearProperty("baseUrl");
        } else {
            System.setProperty("baseUrl", previous);
        }
        restore("allowCartChanges", previousCartOptIn);
        restore("cartProductPath", previousProductPath);
        restore("allowCheckoutLogin", previousLoginOptIn);
        restore("allowCheckoutOrder", previousOrderOptIn);
    }

    private void restore(String key, String value) {
        if (value == null) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, value);
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

    @Test
    public void cartChangesRequireOptInAndRejectPublicStore() {
        System.setProperty("baseUrl", "http://127.0.0.1:8080");
        System.clearProperty("allowCartChanges");
        assertThrows(IllegalStateException.class, TestConfig::requireCartChangesAllowed);
        System.setProperty("allowCartChanges", "true");
        TestConfig.requireCartChangesAllowed();
        for (String origin : new String[] {
                "https://askomdch.com", "https://www.askomdch.com", "https://staging.askomdch.com",
                "https://askomdch.com."
        }) {
            System.setProperty("baseUrl", origin);
            assertThrows(IllegalStateException.class, TestConfig::requireCartChangesAllowed);
        }
    }

    @Test
    public void cartProductMustBeAnExplicitLocalProductPath() {
        System.setProperty("baseUrl", "http://127.0.0.1:8080");
        for (String value : new String[] {"//example.invalid/product/item", "/product/item/?x=1", "/cart/"}) {
            System.setProperty("cartProductPath", value);
            assertThrows(IllegalArgumentException.class, TestConfig::cartProductUrl);
        }
        System.setProperty("cartProductPath", "/product/item/");
        assertEquals("http://127.0.0.1:8080/product/item/", TestConfig.cartProductUrl().toString());
    }

    @Test
    public void cartActionsMustRemainOnConfiguredOrigin() {
        System.setProperty("baseUrl", "https://test.example.invalid");
        TestConfig.requireSameOrigin("https://test.example.invalid/cart/");
        for (String url : new String[] {
                "https://other.example.invalid/cart/", "http://test.example.invalid/cart/",
                "https://test.example.invalid:444/cart/"
        }) {
            assertThrows(IllegalStateException.class, () -> TestConfig.requireSameOrigin(url));
        }
    }

    @Test
    public void checkoutLoginRequiresBothOptInsAndANonProductionOrigin() {
        System.setProperty("baseUrl", "http://127.0.0.1:8080");
        System.setProperty("allowCartChanges", "true");
        System.clearProperty("allowCheckoutLogin");
        assertThrows(IllegalStateException.class, TestConfig::requireCheckoutLoginAllowed);
        System.setProperty("allowCheckoutLogin", "true");
        TestConfig.requireCheckoutLoginAllowed();
        System.setProperty("baseUrl", "https://askomdch.com");
        assertThrows(IllegalStateException.class, TestConfig::requireCheckoutLoginAllowed);
    }

    @Test
    public void checkoutOrdersRequireBothOptInsAndANonProductionOrigin() {
        System.setProperty("baseUrl", "http://127.0.0.1:8080");
        System.clearProperty("allowCartChanges");
        System.setProperty("allowCheckoutOrder", "true");
        assertThrows(IllegalStateException.class, TestConfig::requireCheckoutOrderAllowed);
        System.setProperty("allowCartChanges", "true");
        System.clearProperty("allowCheckoutOrder");
        assertThrows(IllegalStateException.class, TestConfig::requireCheckoutOrderAllowed);
        System.setProperty("allowCheckoutOrder", "true");
        TestConfig.requireCheckoutOrderAllowed();
        System.setProperty("baseUrl", "https://askomdch.com.");
        assertThrows(IllegalStateException.class, TestConfig::requireCheckoutOrderAllowed);
    }
}
