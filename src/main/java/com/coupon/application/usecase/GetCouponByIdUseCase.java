package com.coupon.application.usecase;

import com.coupon.domain.entity.Coupon;
import com.coupon.domain.entity.CouponStatus;
import com.coupon.domain.gateway.CouponGateway;

import java.util.NoSuchElementException;

public class GetCouponByIdUseCase {
    private final CouponGateway couponGateway;

    public GetCouponByIdUseCase(CouponGateway couponGateway) {
        this.couponGateway = couponGateway;
    }

    public Coupon execute(String id) {
        Coupon coupon = couponGateway.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Coupon not found"));

        if (coupon.getStatus() == CouponStatus.DELETED) {
            throw new NoSuchElementException("Coupon is not active");
        }

        return coupon;
    }
}
