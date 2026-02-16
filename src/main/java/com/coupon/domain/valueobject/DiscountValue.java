package com.coupon.domain.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

public class DiscountValue {
    private static final BigDecimal MINIMUM_VALUE = new BigDecimal("0.5");
    private final BigDecimal value;

    public DiscountValue(BigDecimal value) {
        validate(value);
        this.value = value;
    }

    private void validate(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("Discount value is required");
        }

        if (value.compareTo(MINIMUM_VALUE) < 0) {
            throw new IllegalArgumentException("Discount value must be at least 0.5");
        }
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscountValue that = (DiscountValue) o;
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
