package com.askomdch.qa.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class CheckoutFixtures {
    private CheckoutFixtures() {
    }

    public static String required(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Required approved checkout fixture is missing: " + name);
        }
        return value;
    }

    public static String accountUsername() {
        return required("ASKOMDCH_CHECKOUT_USERNAME");
    }

    public static String accountEmail() {
        return required("ASKOMDCH_CHECKOUT_EMAIL");
    }

    public static String accountPassword() {
        return required("ASKOMDCH_CHECKOUT_PASSWORD");
    }

    public static Map<String, String> billing() {
        return Map.of(
                "billing_first_name", required("ASKOMDCH_BILLING_FIRST_NAME"),
                "billing_last_name", required("ASKOMDCH_BILLING_LAST_NAME"),
                "billing_country", required("ASKOMDCH_BILLING_COUNTRY"),
                "billing_address_1", required("ASKOMDCH_BILLING_ADDRESS"),
                "billing_city", required("ASKOMDCH_BILLING_CITY"),
                "billing_state", required("ASKOMDCH_BILLING_STATE"),
                "billing_postcode", required("ASKOMDCH_BILLING_POSTCODE"),
                "billing_phone", required("ASKOMDCH_BILLING_PHONE"),
                "billing_email", required("ASKOMDCH_BILLING_EMAIL"));
    }

    public static Map<String, String> shipping() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("shipping_first_name", required("ASKOMDCH_SHIPPING_FIRST_NAME"));
        values.put("shipping_last_name", required("ASKOMDCH_SHIPPING_LAST_NAME"));
        values.put("shipping_country", required("ASKOMDCH_SHIPPING_COUNTRY"));
        values.put("shipping_address_1", required("ASKOMDCH_SHIPPING_ADDRESS"));
        values.put("shipping_city", required("ASKOMDCH_SHIPPING_CITY"));
        values.put("shipping_state", required("ASKOMDCH_SHIPPING_STATE"));
        values.put("shipping_postcode", required("ASKOMDCH_SHIPPING_POSTCODE"));
        String notes = System.getenv("ASKOMDCH_SHIPPING_NOTES");
        if (notes != null) {
            values.put("order_comments", notes);
        }
        return Map.copyOf(values);
    }

    public static String uniqueEmail() {
        return required("ASKOMDCH_CHECKOUT_UNIQUE_EMAIL");
    }

    public static String registeredEmail() {
        return required("ASKOMDCH_CHECKOUT_REGISTERED_EMAIL");
    }

    public static String invalidAccountPassword(String validation) {
        String env = "ASKOMDCH_ACCOUNT_PASSWORD_" + validation.toUpperCase().replace('-', '_');
        return required(env);
    }

    public static Map<String, String> billingWithEmail(String email) {
        Map<String, String> values = new LinkedHashMap<>(billing());
        values.put("billing_email", email);
        return Map.copyOf(values);
    }

    public static String uniqueEmailFixture() {
        String template = uniqueEmail();
        if (!template.contains("{uuid}")) {
            throw new IllegalArgumentException("ASKOMDCH_CHECKOUT_UNIQUE_EMAIL must contain the {uuid} token.");
        }
        return template.replace("{uuid}", UUID.randomUUID().toString());
    }

    public static Map<String, String> billingAtMaximums() {
        Map<String, String> values = new LinkedHashMap<>(billing());
        values.put("billing_first_name", "A".repeat(50));
        values.put("billing_last_name", "B".repeat(50));
        values.put("billing_city", "C".repeat(50));
        values.put("billing_address_1", "1".repeat(100));
        values.put("billing_phone", "1".repeat(15));
        return Map.copyOf(values);
    }

    public static Map<String, String> shippingAtMaximums() {
        Map<String, String> values = new LinkedHashMap<>(shipping());
        values.put("shipping_first_name", "A".repeat(50));
        values.put("shipping_last_name", "B".repeat(50));
        values.put("shipping_city", "C".repeat(50));
        values.put("shipping_address_1", "1".repeat(100));
        values.put("order_comments", "N".repeat(500));
        return Map.copyOf(values);
    }
}
