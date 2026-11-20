package com.askomdch.qa.config;

import java.util.UUID;

public final class RegistrationFixtures {
    private RegistrationFixtures() {
    }

    public static String required(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Required approved synthetic fixture is missing: " + key);
        }
        return value;
    }

    public static String usernameTemplate() {
        return required("ASKOMDCH_REGISTRATION_USERNAME_TEMPLATE");
    }

    public static String uniqueUsername(int length, boolean mixedCase) {
        String template = usernameTemplate();
        if (!"{letters}".equals(template)) {
            throw new IllegalArgumentException(
                    "ASKOMDCH_REGISTRATION_USERNAME_TEMPLATE must be exactly {letters}.");
        }
        String hex = UUID.randomUUID().toString().replace("-", "");
        StringBuilder alphabetic = new StringBuilder(hex.length());
        for (int index = 0; index < hex.length(); index++) {
            alphabetic.append((char) ('a' + Character.digit(hex.charAt(index), 16)));
        }
        String result = alphabetic.substring(0, length);
        if (result.length() != length || !result.matches("[A-Za-z]+")) {
            throw new IllegalArgumentException("Username template must produce letters-only names of requested length.");
        }
        if (mixedCase) {
            result = result.substring(0, 1).toUpperCase() + result.substring(1).toLowerCase();
        }
        return result;
    }

    public static String uniqueEmail() {
        String template = required("ASKOMDCH_REGISTRATION_EMAIL_TEMPLATE");
        if (!template.contains("{uuid}")) {
            throw new IllegalArgumentException(
                    "ASKOMDCH_REGISTRATION_EMAIL_TEMPLATE must contain the {uuid} token.");
        }
        return template.replace("{uuid}", UUID.randomUUID().toString());
    }

    public static String validPassword() {
        return required("ASKOMDCH_REGISTRATION_PASSWORD_VALID");
    }

    public static String existingUsername() {
        return required("ASKOMDCH_REGISTRATION_EXISTING_USERNAME");
    }

    public static String existingEmail() {
        return required("ASKOMDCH_REGISTRATION_EXISTING_EMAIL");
    }
}
