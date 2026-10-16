package com.flower.mapper;

import com.flower.entity.UserCoupon;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserCouponMapper {


    // 插入用户优惠券

    @Insert("INSERT INTO user_coupon(user_id, coupon_id, status, expire_at) " +
            "VALUES(#{userId}, #{couponId}, #{status}, DATE_ADD(NOW(), INTERVAL #{expireDays} DAY))")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserCoupon userCoupon);


    // 获取用户未使用的优惠券（未过期）

    @Select("SELECT uc.*, c.name as coupon_name, c.type as coupon_type, c.value as coupon_value, c.min_amount as coupon_minAmount " +
            "FROM user_coupon uc JOIN coupon c ON uc.coupon_id = c.id " +
            "WHERE uc.user_id = #{userId} AND uc.status = 0 AND uc.expire_at > NOW() " +
            "ORDER BY uc.created_at DESC")
    List<UserCoupon> findUnusedByUserId(Long userId);

    // 获取用户所有优惠券

    @Select("SELECT uc.*, c.name as coupon_name, c.type as coupon_type, c.value as coupon_value, c.min_amount as coupon_minAmount " +
            "FROM user_coupon uc JOIN coupon c ON uc.coupon_id = c.id " +
            "WHERE uc.user_id = #{userId} ORDER BY uc.created_at DESC")
    List<UserCoupon> findAllByUserId(Long userId);


    //标记优惠券为已使用

    @Update("UPDATE user_coupon SET status = 1, used_at = NOW() WHERE id = #{id}")
    void markUsed(Long id);
}