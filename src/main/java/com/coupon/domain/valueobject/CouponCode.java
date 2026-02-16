package com.coupon.domain.valueobject;

import java.util.Objects;

public class CouponCode {
    private static final int CODE_LENGTH = 6;
    private final String value;

    public CouponCode(String code) {
        this.value = sanitizeAndValidate(code);
    }

    private String sanitizeAndValidate(String code) {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("Code is required");
        }

        String sanitized = sanitize(code);

        if (sanitized.length() != CODE_LENGTH) {
            throw new IllegalArgumentException("Code must be exactly 6 characters after sanitization");
        }

        return sanitized;
    }

    private String sanitize(String code) {
        String cleaned = code.replaceAll("[^a-zA-Z0-9]", "");
        return cleaned.length() > CODE_LENGTH
            ? cleaned.substring(0, CODE_LENGTH)
            : cleaned;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CouponCode that = (CouponCode) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
