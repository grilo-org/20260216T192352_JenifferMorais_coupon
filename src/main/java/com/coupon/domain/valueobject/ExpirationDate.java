package com.coupon.domain.valueobject;

import java.time.LocalDateTime;
import java.util.Objects;

public class ExpirationDate {
    private final LocalDateTime value;

    public ExpirationDate(LocalDateTime value) {
        validate(value);
        this.value = value;
    }

    private ExpirationDate(LocalDateTime value, boolean skipValidation) {
        if (!skipValidation) validate(value);
        this.value = value;
    }

    public static ExpirationDate reconstitute(LocalDateTime value) {
        return new ExpirationDate(value, true);
    }

    private void validate(LocalDateTime value) {
        if (value == null) {
            throw new IllegalArgumentException("Expiration date is required");
        }

        if (value.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Expiration date cannot be in the past");
        }
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(value);
    }

    public LocalDateTime getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpirationDate that = (ExpirationDate) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
