package com.coupon.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CouponCodeTest {

    @Test
    void shouldCreateValidCouponCode() {
        CouponCode code = new CouponCode("ABC123");
        assertEquals("ABC123", code.getValue());
    }

    @Test
    void shouldSanitizeSpecialCharacters() {
        CouponCode code = new CouponCode("AB@C#1$23");
        assertEquals("ABC123", code.getValue());
    }

    @Test
    void shouldTruncateLongCode() {
        CouponCode code = new CouponCode("ABCDEFGHIJKLMNOP");
        assertEquals("ABCDEF", code.getValue());
    }

    @Test
    void shouldThrowExceptionWhenCodeIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new CouponCode(null));
    }

    @Test
    void shouldThrowExceptionWhenCodeIsTooShort() {
        assertThrows(IllegalArgumentException.class, () -> new CouponCode("ABC"));
    }
}
