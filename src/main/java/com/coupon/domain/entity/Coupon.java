package com.coupon.domain.entity;

import com.coupon.domain.valueobject.CouponCode;
import com.coupon.domain.valueobject.DiscountValue;
import com.coupon.domain.valueobject.ExpirationDate;

import java.time.LocalDateTime;

public class Coupon {
    private String id;
    private CouponCode code;
    private String description;
    private DiscountValue discountValue;
    private ExpirationDate expirationDate;
    private CouponStatus status;
    private boolean published;
    private boolean redeemed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Coupon(String code, String description, String discountValue,
                  LocalDateTime expirationDate, boolean published) {
        this.code = new CouponCode(code);
        this.description = validateDescription(description);
        this.discountValue = createDiscountValue(discountValue);
        this.expirationDate = new ExpirationDate(expirationDate);
        this.published = published;
        this.status = CouponStatus.ACTIVE;
        this.redeemed = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    private DiscountValue createDiscountValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Discount value is required");
        }
        return new DiscountValue(new java.math.BigDecimal(value));
    }

    private Coupon(String id, CouponCode code, String description, DiscountValue discountValue,
                   ExpirationDate expirationDate, CouponStatus status, boolean published,
                   boolean redeemed, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discountValue = discountValue;
        this.expirationDate = expirationDate;
        this.status = status;
        this.published = published;
        this.redeemed = redeemed;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Coupon reconstitute(String id, String code, String description,
                                       String discountValue, LocalDateTime expirationDate,
                                       CouponStatus status, boolean published, boolean redeemed,
                                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Coupon(
            id,
            new CouponCode(code),
            description,
            new DiscountValue(new java.math.BigDecimal(discountValue)),
            ExpirationDate.reconstitute(expirationDate),
            status,
            published,
            redeemed,
            createdAt,
            updatedAt
        );
    }

    private String validateDescription(String description) {
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description is required");
        }
        return description;
    }

    public void delete() {
        if (this.status == CouponStatus.DELETED) {
            throw new IllegalStateException("Coupon is already deleted");
        }
        this.status = CouponStatus.DELETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void publish() {
        if (this.status == CouponStatus.DELETED) {
            throw new IllegalStateException("Cannot publish a deleted coupon");
        }
        this.published = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void unpublish() {
        if (this.status == CouponStatus.DELETED) {
            throw new IllegalStateException("Cannot unpublish a deleted coupon");
        }
        this.published = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void redeem() {
        if (this.status == CouponStatus.DELETED) {
            throw new IllegalStateException("Cannot redeem a deleted coupon");
        }
        if (!this.published) {
            throw new IllegalStateException("Cannot redeem an unpublished coupon");
        }
        if (this.redeemed) {
            throw new IllegalStateException("Coupon has already been redeemed");
        }
        if (this.expirationDate.isExpired()) {
            throw new IllegalStateException("Cannot redeem an expired coupon");
        }
        this.redeemed = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        if (this.status == CouponStatus.DELETED) {
            throw new IllegalStateException("Cannot activate a deleted coupon");
        }
        this.status = CouponStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        if (this.status == CouponStatus.DELETED) {
            throw new IllegalStateException("Cannot deactivate a deleted coupon");
        }
        this.status = CouponStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isValid() {
        return this.status == CouponStatus.ACTIVE
                && this.published
                && !this.redeemed
                && !this.expirationDate.isExpired();
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getCodeValue() {
        return code.getValue();
    }

    public String getDescription() {
        return description;
    }

    public java.math.BigDecimal getDiscountValueAmount() {
        return discountValue.getValue();
    }

    public LocalDateTime getExpirationDateValue() {
        return expirationDate.getValue();
    }

    public CouponStatus getStatus() {
        return status;
    }

    public boolean isPublished() {
        return published;
    }

    public boolean isRedeemed() {
        return redeemed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Getters para Value Objects (para uso interno)
    CouponCode getCode() {
        return code;
    }

    DiscountValue getDiscountValue() {
        return discountValue;
    }

    ExpirationDate getExpirationDate() {
        return expirationDate;
    }
}
