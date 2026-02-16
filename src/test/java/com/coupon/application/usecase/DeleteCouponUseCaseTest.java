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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCouponUseCaseTest {

    @Mock
    private CouponGateway gateway;

    @InjectMocks
    private DeleteCouponUseCase useCase;

    @Test
    void shouldDeleteCouponSuccessfully() {
        Coupon coupon = new Coupon(
                "ABC123",
                "Test",
                "10.0",
                LocalDateTime.now().plusDays(5),
                false
        );

        when(gateway.findById("valid-id")).thenReturn(Optional.of(coupon));
        when(gateway.save(any(Coupon.class))).thenAnswer(i -> i.getArguments()[0]);

        useCase.execute("valid-id");

        verify(gateway, times(1)).findById("valid-id");
        verify(gateway, times(1)).save(any(Coupon.class));
    }

    @Test
    void shouldThrowExceptionWhenCouponNotFound() {
        when(gateway.findById("invalid-id")).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> useCase.execute("invalid-id")
        );

        assertEquals("Coupon not found", exception.getMessage());
        verify(gateway, times(1)).findById("invalid-id");
        verify(gateway, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenDeletingAlreadyDeletedCoupon() {
        Coupon coupon = new Coupon(
                "ABC123",
                "Test",
                "10.0",
                LocalDateTime.now().plusDays(5),
                false
        );
        coupon.delete();

        when(gateway.findById("valid-id")).thenReturn(Optional.of(coupon));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> useCase.execute("valid-id")
        );

        assertEquals("Coupon is already deleted", exception.getMessage());
        verify(gateway, never()).save(any());
    }
}
