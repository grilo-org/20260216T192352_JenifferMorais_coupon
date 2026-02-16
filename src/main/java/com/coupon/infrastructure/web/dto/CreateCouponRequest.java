package com.coupon.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new coupon")
public class CreateCouponRequest {
    @NotBlank(message = "Code is required")
    @Schema(description = "Coupon code (alphanumeric, 6 characters after sanitization)", example = "ABC-123", required = true)
    private String code;

    @NotBlank(message = "Description is required")
    @Schema(description = "Coupon description", required = true)
    private String description;

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.5", message = "Discount value must be at least 0.5")
    @Schema(description = "Discount value (minimum 0.5)", example = "0.8", required = true)
    private BigDecimal discountValue;

    @NotNull(message = "Expiration date is required")
    @Schema(description = "Expiration date (cannot be in the past)", example = "2025-11-04T17:14:45.180Z", required = true)
    private LocalDateTime expirationDate;

    @Schema(description = "Whether the coupon is published", example = "false", defaultValue = "false")
    private Boolean published;
}
