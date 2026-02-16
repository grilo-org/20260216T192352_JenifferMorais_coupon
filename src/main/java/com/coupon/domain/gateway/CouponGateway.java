package com.coupon.domain.gateway;

import com.coupon.domain.entity.Coupon;

import java.util.Optional;

public interface CouponGateway {
    Coupon save(Coupon coupon);
    Optional<Coupon> findById(String id);
    boolean existsByCode(String code);
}
