package com.flower.entity;

import java.time.LocalDateTime;

public class UserCoupon {
    private Long id;
    private Long userId;
    private Integer couponId;
    private Integer status;
    private LocalDateTime expireAt;
    private LocalDateTime usedAt;
    private LocalDateTime createdAt;
    private Integer expireDays;

    // 从JOIN查询得到的字段
    private String couponName;
    private String couponType;
    private Double couponValue;
    private Double couponMinAmount;

    // 完整的优惠券对象（可选）
    private Coupon coupon;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getCouponId() { return couponId; }
    public void setCouponId(Integer couponId) { this.couponId = couponId; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getExpireAt() { return expireAt; }
    public void setExpireAt(LocalDateTime expireAt) { this.expireAt = expireAt; }

    public LocalDateTime getUsedAt() { return usedAt; }
    public void setUsedAt(LocalDateTime usedAt) { this.usedAt = usedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getExpireDays() { return expireDays; }
    public void setExpireDays(Integer expireDays) { this.expireDays = expireDays; }

    public String getCouponName() { return couponName; }
    public void setCouponName(String couponName) { this.couponName = couponName; }

    public String getCouponType() { return couponType; }
    public void setCouponType(String couponType) { this.couponType = couponType; }

    public Double getCouponValue() { return couponValue; }
    public void setCouponValue(Double couponValue) { this.couponValue = couponValue; }

    public Double getCouponMinAmount() { return couponMinAmount; }
    public void setCouponMinAmount(Double couponMinAmount) { this.couponMinAmount = couponMinAmount; }

    public Coupon getCoupon() {
        if (coupon == null && couponName != null) {
            coupon = new Coupon();
            coupon.setName(couponName);
            coupon.setType(couponType);
            if (couponValue != null) {
                coupon.setValue(java.math.BigDecimal.valueOf(couponValue));
            }
            if (couponMinAmount != null) {
                coupon.setMinAmount(java.math.BigDecimal.valueOf(couponMinAmount));
            }
        }
        return coupon;
    }
    public void setCoupon(Coupon coupon) { this.coupon = coupon; }
}