package com.coupon.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ExpirationDateTest {

    @Test
    void shouldCreateValidExpirationDate() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(7);
        ExpirationDate date = new ExpirationDate(futureDate);
        assertEquals(futureDate, date.getValue());
    }

    @Test
    void shouldThrowExceptionWhenDateIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new ExpirationDate(null));
    }

    @Test
    void shouldThrowExceptionWhenDateIsInThePast() {
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);
        assertThrows(IllegalArgumentException.class, () -> new ExpirationDate(pastDate));
    }

    @Test
    void shouldNotBeExpiredWhenDateIsInFuture() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(7);
        ExpirationDate date = new ExpirationDate(futureDate);
        assertFalse(date.isExpired());
    }
}
