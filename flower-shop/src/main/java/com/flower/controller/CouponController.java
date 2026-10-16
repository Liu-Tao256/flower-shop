package com.flower.controller;

import com.flower.entity.Coupon;
import com.flower.entity.UserCoupon;
import com.flower.mapper.CouponMapper;
import com.flower.mapper.UserCouponMapper;
import com.flower.mapper.UserMapper;
import com.flower.utils.JwtUtil;
import com.flower.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CouponController {

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Autowired
    private UserMapper userMapper;

    private Long getUserId(HttpServletRequest request) {
        return JwtUtil.getUserIdFromRequest(request);
    }

    @GetMapping("/coupons")
    public Result<List<Coupon>> getCoupons() {
        return Result.success(couponMapper.findAllAvailable());
    }

    @GetMapping("/user-coupons")
    public Result<List<UserCoupon>> getUserCoupons(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error("未登录");
        }
        return Result.success(userCouponMapper.findAllByUserId(userId));
    }

    @PostMapping("/exchange/coupon/{couponId}")
    public Result<?> exchangeCoupon(@PathVariable Integer couponId, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error("未登录");
        }

        Coupon coupon = couponMapper.findById(couponId);
        if (coupon == null) {
            return Result.error("优惠券不存在");
        }

        // 检查用户积分
        int userPoints = userMapper.findPointsByUserId(userId);
        System.out.println("用户积分: " + userPoints + ", 所需积分: " + coupon.getPointsCost());

        if (userPoints < coupon.getPointsCost()) {
            return Result.error("积分不足，当前积分: " + userPoints);
        }

        // 扣减积分
        userMapper.deductPoints(userId, coupon.getPointsCost());

        // 发放优惠券
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setStatus(0);
        userCoupon.setExpireDays(coupon.getExpireDays());  // 设置有效期天数
        userCouponMapper.insert(userCoupon);

        // 更新优惠券已兑换数量
        couponMapper.incrementUsedQuantity(couponId);

        return Result.success("兑换成功");
    }
}