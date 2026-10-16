package com.flower.service;

import com.flower.entity.Coupon;
import com.flower.mapper.CouponMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponService {

    @Autowired
    private CouponMapper couponMapper;

    // ========== 用户端方法 ==========


     // 获取可兑换的优惠券列表（仅上架）

    public List<Coupon> getAvailableCoupons() {
        return couponMapper.findAllAvailable();
    }


    // 根据ID获取优惠券

    public Coupon getCouponById(Integer id) {
        return couponMapper.findById(id);
    }

    // ========== 管理后台方法 ==========


     // 获取所有优惠券（管理后台）

    public List<Coupon> getAllCouponsForAdmin() {
        return couponMapper.findAllForAdmin();
    }


    // 新增优惠券

    public void addCoupon(Coupon coupon) {
        couponMapper.insert(coupon);
    }


    // 更新优惠券

    public void updateCoupon(Coupon coupon) {
        couponMapper.update(coupon);
    }


    // 删除优惠券

    public void deleteCoupon(Integer id) {
        couponMapper.deleteById(id);
    }


    // 增加已使用数量

    public void incrementUsedQuantity(Integer id) {
        couponMapper.incrementUsedQuantity(id);
    }
}