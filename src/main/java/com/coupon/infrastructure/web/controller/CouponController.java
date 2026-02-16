package com.coupon.infrastructure.web.controller;

import com.coupon.domain.entity.Coupon;
import com.coupon.application.usecase.CreateCouponUseCase;
import com.coupon.application.usecase.DeleteCouponUseCase;
import com.coupon.application.usecase.GetCouponByIdUseCase;
import com.coupon.infrastructure.web.dto.CreateCouponRequest;
import com.coupon.infrastructure.web.dto.CouponResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupon")
@Tag(name = "Coupon")
public class CouponController {
    private final CreateCouponUseCase createCouponUseCase;
    private final DeleteCouponUseCase deleteCouponUseCase;
    private final GetCouponByIdUseCase getCouponByIdUseCase;

    public CouponController(CreateCouponUseCase createCouponUseCase,
                            DeleteCouponUseCase deleteCouponUseCase,
                            GetCouponByIdUseCase getCouponByIdUseCase) {
        this.createCouponUseCase = createCouponUseCase;
        this.deleteCouponUseCase = deleteCouponUseCase;
        this.getCouponByIdUseCase = getCouponByIdUseCase;
    }

    @PostMapping
    @Operation(summary = "Create coupon")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<CouponResponse> create(@Valid @RequestBody CreateCouponRequest request) {
        Coupon coupon = createCouponUseCase.execute(
                request.getCode(),
                request.getDescription(),
                request.getDiscountValue().toString(),
                request.getExpirationDate(),
                request.getPublished()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(coupon));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get coupon by ID")
    @ApiResponse(responseCode = "200")
    public ResponseEntity<CouponResponse> getById(@PathVariable String id) {
        Coupon coupon = getCouponByIdUseCase.execute(id);
        return ResponseEntity.ok(toResponse(coupon));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete coupon")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        deleteCouponUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    private CouponResponse toResponse(Coupon coupon) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getCodeValue(),
                coupon.getDescription(),
                coupon.getDiscountValueAmount(),
                coupon.getExpirationDateValue(),
                coupon.getStatus().name(),
                coupon.isPublished(),
                coupon.isRedeemed()
        );
    }
}
