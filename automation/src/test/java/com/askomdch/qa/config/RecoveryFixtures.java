package com.askomdch.qa.config;

public final class RecoveryFixtures {
    private RecoveryFixtures() {
    }

    public static String required(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Required approved recovery fixture is missing: " + key);
        }
        return value;
    }

    public static String username() {
        return required("ASKOMDCH_RECOVERY_USERNAME");
    }

    public static String email() {
        return required("ASKOMDCH_RECOVERY_EMAIL");
    }

    public static String oldPassword() {
        return required("ASKOMDCH_RECOVERY_OLD_PASSWORD");
    }

    public static String newPassword() {
        return required("ASKOMDCH_RECOVERY_NEW_PASSWORD");
    }
}
