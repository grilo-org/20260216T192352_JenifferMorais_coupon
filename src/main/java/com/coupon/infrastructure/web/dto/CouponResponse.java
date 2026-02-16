package com.coupon.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Coupon response")
public class CouponResponse {
    @Schema(description = "Coupon unique identifier", example = "cef9d1e3-aae5-4ab6-a297-358c6032b1e7", required = true)
    private String id;

    @Schema(description = "Coupon code (6 alphanumeric characters)", example = "ABC123", required = true)
    private String code;

    @Schema(description = "Coupon description", required = true)
    private String description;

    @Schema(description = "Discount value", example = "0.8", required = true)
    private BigDecimal discountValue;

    @Schema(description = "Expiration date", required = true)
    private LocalDateTime expirationDate;

    @Schema(description = "Coupon status", example = "ACTIVE", required = true)
    private String status;

    @Schema(description = "Whether the coupon is published", example = "false", required = true, defaultValue = "false")
    private boolean published;

    @Schema(description = "Whether the coupon has been redeemed", example = "false", required = true, defaultValue = "false")
    private boolean redeemed;
}
