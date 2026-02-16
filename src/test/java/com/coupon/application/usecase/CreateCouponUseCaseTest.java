package com.coupon.application.usecase;

import com.coupon.domain.entity.Coupon;
import com.coupon.domain.gateway.CouponGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCouponUseCaseTest {

    @Mock
    private CouponGateway gateway;

    @InjectMocks
    private CreateCouponUseCase useCase;

    @Test
    void shouldCreateCouponWithValidData() {
        when(gateway.save(any(Coupon.class))).thenAnswer(i -> i.getArguments()[0]);

        Coupon result = useCase.execute(
                "ABC123",
                "Test coupon",
                "10.5",
                LocalDateTime.now().plusDays(5),
                false
        );

        assertNotNull(result);
        assertEquals("ABC123", result.getCodeValue());
        assertEquals("Test coupon", result.getDescription());
        verify(gateway, times(1)).save(any(Coupon.class));
    }

    @Test
    void shouldSanitizeCodeBeforeCreating() {
        when(gateway.save(any(Coupon.class))).thenAnswer(i -> i.getArguments()[0]);

        Coupon result = useCase.execute(
                "AB@C#123",
                "Test",
                "10.0",
                LocalDateTime.now().plusDays(5),
                false
        );

        assertEquals("ABC123", result.getCodeValue());
    }

    @Test
    void shouldRejectInvalidDiscountValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            useCase.execute(
                    "ABC123",
                    "Test",
                    "0.3",
                    LocalDateTime.now().plusDays(5),
                    false
            );
        });

        verify(gateway, never()).save(any());
    }

    @Test
    void shouldRejectPastExpirationDate() {
        assertThrows(IllegalArgumentException.class, () -> {
            useCase.execute(
                    "ABC123",
                    "Test",
                    "10.0",
                    LocalDateTime.now().minusDays(1),
                    false
            );
        });

        verify(gateway, never()).save(any());
    }

    @Test
    void shouldRejectNullDescription() {
        assertThrows(IllegalArgumentException.class, () -> {
            useCase.execute(
                    "ABC123",
                    null,
                    "10.0",
                    LocalDateTime.now().plusDays(5),
                    false
            );
        });

        verify(gateway, never()).save(any());
    }

    @Test
    void shouldRejectDuplicateCode() {
        when(gateway.existsByCode("ABC123")).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            useCase.execute(
                    "ABC123",
                    "Test",
                    "10.0",
                    LocalDateTime.now().plusDays(5),
                    false
            );
        });

        assertEquals("Coupon with code 'ABC123' already exists. Only the first 6 alphanumeric characters are considered", exception.getMessage());
        verify(gateway, never()).save(any());
    }

    @Test
    void shouldCreateAsPublishedWhenRequested() {
        when(gateway.save(any(Coupon.class))).thenAnswer(i -> i.getArguments()[0]);

        Coupon result = useCase.execute(
                "ABC123",
                "Test",
                "10.0",
                LocalDateTime.now().plusDays(5),
                true
        );

        assertTrue(result.isPublished());
    }
}
