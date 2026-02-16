package com.coupon.infrastructure.persistence.gateway;

import com.coupon.domain.entity.Coupon;
import com.coupon.domain.gateway.CouponGateway;
import com.coupon.infrastructure.persistence.entity.CouponEntity;
import com.coupon.infrastructure.persistence.repository.CouponRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CouponGatewayImpl implements CouponGateway {
    private final CouponRepository repository;

    public CouponGatewayImpl(CouponRepository repository) {
        this.repository = repository;
    }

    @Override
    public Coupon save(Coupon coupon) {
        CouponEntity entity = toEntity(coupon);
        CouponEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Coupon> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        return repository.existsByCode(code);
    }

    private CouponEntity toEntity(Coupon coupon) {
        return new CouponEntity(
                coupon.getId(),
                coupon.getCodeValue(),
                coupon.getDescription(),
                coupon.getDiscountValueAmount(),
                coupon.getExpirationDateValue(),
                coupon.getStatus(),
                coupon.isPublished(),
                coupon.isRedeemed(),
                coupon.getCreatedAt(),
                coupon.getUpdatedAt()
        );
    }

    private Coupon toDomain(CouponEntity entity) {
        return Coupon.reconstitute(
                entity.getId(),
                entity.getCode(),
                entity.getDescription(),
                entity.getDiscountValue().toString(),
                entity.getExpirationDate(),
                entity.getStatus(),
                entity.isPublished(),
                entity.isRedeemed(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
