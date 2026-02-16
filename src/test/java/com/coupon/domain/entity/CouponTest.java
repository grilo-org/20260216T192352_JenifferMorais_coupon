package com.coupon.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CouponTest {

    private Coupon createActiveCoupon() {
        return new Coupon("ABC123", "Test", "10.0", LocalDateTime.now().plusDays(5), false);
    }

    private Coupon createPublishedCoupon() {
        return new Coupon("ABC123", "Test", "10.0", LocalDateTime.now().plusDays(5), true);
    }

    private Coupon createDeletedCoupon() {
        Coupon coupon = createActiveCoupon();
        coupon.delete();
        return coupon;
    }

    // publish
    @Test
    void shouldPublishCoupon() {
        Coupon coupon = createActiveCoupon();
        coupon.publish();
        assertTrue(coupon.isPublished());
    }

    @Test
    void shouldNotPublishDeletedCoupon() {
        Coupon coupon = createDeletedCoupon();
        assertThrows(IllegalStateException.class, coupon::publish);
    }

    // unpublish
    @Test
    void shouldUnpublishCoupon() {
        Coupon coupon = createPublishedCoupon();
        coupon.unpublish();
        assertFalse(coupon.isPublished());
    }

    @Test
    void shouldNotUnpublishDeletedCoupon() {
        Coupon coupon = createDeletedCoupon();
        assertThrows(IllegalStateException.class, coupon::unpublish);
    }

    // redeem
    @Test
    void shouldRedeemPublishedCoupon() {
        Coupon coupon = createPublishedCoupon();
        coupon.redeem();
        assertTrue(coupon.isRedeemed());
    }

    @Test
    void shouldNotRedeemDeletedCoupon() {
        Coupon coupon = createDeletedCoupon();
        assertThrows(IllegalStateException.class, coupon::redeem);
    }

    @Test
    void shouldNotRedeemUnpublishedCoupon() {
        Coupon coupon = createActiveCoupon();
        assertThrows(IllegalStateException.class, coupon::redeem);
    }

    @Test
    void shouldNotRedeemAlreadyRedeemedCoupon() {
        Coupon coupon = createPublishedCoupon();
        coupon.redeem();
        assertThrows(IllegalStateException.class, coupon::redeem);
    }

    @Test
    void shouldNotRedeemExpiredCoupon() {
        Coupon coupon = Coupon.reconstitute(
                "id-1", "ABC123", "Test", "10.0",
                LocalDateTime.now().minusDays(1),
                CouponStatus.ACTIVE, true, false,
                LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(10)
        );
        assertThrows(IllegalStateException.class, coupon::redeem);
    }

    // activate / deactivate
    @Test
    void shouldDeactivateCoupon() {
        Coupon coupon = createActiveCoupon();
        coupon.deactivate();
        assertEquals(CouponStatus.INACTIVE, coupon.getStatus());
    }

    @Test
    void shouldActivateInactiveCoupon() {
        Coupon coupon = createActiveCoupon();
        coupon.deactivate();
        coupon.activate();
        assertEquals(CouponStatus.ACTIVE, coupon.getStatus());
    }

    @Test
    void shouldNotActivateDeletedCoupon() {
        Coupon coupon = createDeletedCoupon();
        assertThrows(IllegalStateException.class, coupon::activate);
    }

    @Test
    void shouldNotDeactivateDeletedCoupon() {
        Coupon coupon = createDeletedCoupon();
        assertThrows(IllegalStateException.class, coupon::deactivate);
    }

    // isValid
    @Test
    void shouldBeValidWhenActivePublishedNotRedeemedNotExpired() {
        Coupon coupon = createPublishedCoupon();
        assertTrue(coupon.isValid());
    }

    @Test
    void shouldNotBeValidWhenUnpublished() {
        Coupon coupon = createActiveCoupon();
        assertFalse(coupon.isValid());
    }

    @Test
    void shouldNotBeValidWhenDeleted() {
        Coupon coupon = createPublishedCoupon();
        coupon.delete();
        assertFalse(coupon.isValid());
    }

    @Test
    void shouldNotBeValidWhenRedeemed() {
        Coupon coupon = createPublishedCoupon();
        coupon.redeem();
        assertFalse(coupon.isValid());
    }
}
