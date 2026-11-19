package com.askomdch.qa.pages;

import java.math.BigDecimal;

final class UsdPrice {
    private UsdPrice() {
    }

    static BigDecimal parse(String value) {
        String text = value.trim();
        if (!text.matches("\\$\\d+(?:,\\d{3})*(?:\\.\\d{2})?")) {
            throw new IllegalStateException("Unsupported USD price format: " + text);
        }
        return new BigDecimal(text.substring(1).replace(",", ""));
    }
}
