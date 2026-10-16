package com.flower.controller;

import com.flower.entity.Coupon;
import com.flower.service.CouponService;
import com.flower.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/coupons")
public class AdminCouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping
    public Result<List<Coupon>> getAllCoupons() {
        return Result.success(couponService.getAllCouponsForAdmin());
    }


    @GetMapping("/{id}")
    public Result<Coupon> getCouponById(@PathVariable Integer id) {
        Coupon coupon = couponService.getCouponById(id);
        if (coupon == null) {
            return Result.error("优惠券不存在");
        }
        return Result.success(coupon);
    }

    @PostMapping
    public Result<?> addCoupon(@RequestBody Coupon coupon) {
        try {
            couponService.addCoupon(coupon);
            return Result.success("添加成功");
        } catch (Exception e) {
            return Result.error("添加失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<?> updateCoupon(@PathVariable Integer id, @RequestBody Coupon coupon) {
        try {
            coupon.setId(id);
            couponService.updateCoupon(coupon);
            return Result.success("修改成功");
        } catch (Exception e) {
            return Result.error("修改失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteCoupon(@PathVariable Integer id) {
        try {
            couponService.deleteCoupon(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}