package com.coupon.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DiscountValueTest {

    @Test
    void shouldCreateValidDiscountValue() {
        DiscountValue value = new DiscountValue(new BigDecimal("10.50"));
        assertEquals(new BigDecimal("10.50"), value.getValue());
    }

    @Test
    void shouldAcceptMinimumValue() {
        DiscountValue value = new DiscountValue(new BigDecimal("0.5"));
        assertEquals(new BigDecimal("0.5"), value.getValue());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new DiscountValue(null));
    }

    @Test
    void shouldThrowExceptionWhenValueIsBelowMinimum() {
        assertThrows(IllegalArgumentException.class, () -> new DiscountValue(new BigDecimal("0.4")));
    }
}
