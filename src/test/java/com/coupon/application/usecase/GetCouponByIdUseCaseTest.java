package com.coupon.application.usecase;

import com.coupon.domain.entity.Coupon;
import com.coupon.domain.gateway.CouponGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCouponByIdUseCaseTest {

    @Mock
    private CouponGateway gateway;

    @InjectMocks
    private GetCouponByIdUseCase useCase;

    @Test
    void shouldReturnCouponWhenExists() {
        Coupon coupon = new Coupon(
                "ABC123",
                "Test",
                "10.0",
                LocalDateTime.now().plusDays(5),
                false
        );

        when(gateway.findById("valid-id")).thenReturn(Optional.of(coupon));

        Coupon result = useCase.execute("valid-id");

        assertNotNull(result);
        assertEquals("ABC123", result.getCodeValue());
        verify(gateway, times(1)).findById("valid-id");
    }

    @Test
    void shouldThrowExceptionWhenNotFound() {
        when(gateway.findById("invalid-id")).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> useCase.execute("invalid-id")
        );

        assertEquals("Coupon not found", exception.getMessage());
        verify(gateway, times(1)).findById("invalid-id");
    }

    @Test
    void shouldThrowExceptionWhenCouponIsDeleted() {
        Coupon coupon = new Coupon(
                "ABC123",
                "Test",
                "10.0",
                LocalDateTime.now().plusDays(5),
                false
        );
        coupon.delete();

        when(gateway.findById("deleted-id")).thenReturn(Optional.of(coupon));

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> useCase.execute("deleted-id")
        );

        assertEquals("Coupon is not active", exception.getMessage());
    }
}
