package com.flower.mapper;

import com.flower.entity.Coupon;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CouponMapper {

    @Select("SELECT * FROM coupon WHERE status = 1 ORDER BY points_cost")
    List<Coupon> findAllAvailable();

    @Select("SELECT * FROM coupon WHERE id = #{id}")
    Coupon findById(Integer id);

    @Update("UPDATE coupon SET used_quantity = used_quantity + 1 WHERE id = #{id}")
    void incrementUsedQuantity(Integer id);

    // 管理后台：查询所有优惠券
    @Select("SELECT * FROM coupon ORDER BY id")
    List<Coupon> findAllForAdmin();

    // 新增优惠券
    @Insert("INSERT INTO coupon(name, type, value, min_amount, points_cost, expire_days, total_quantity, status) " +
            "VALUES(#{name}, #{type}, #{value}, #{minAmount}, #{pointsCost}, #{expireDays}, #{totalQuantity}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Coupon coupon);

    // 更新优惠券
    @Update("UPDATE coupon SET name=#{name}, type=#{type}, value=#{value}, min_amount=#{minAmount}, " +
            "points_cost=#{pointsCost}, expire_days=#{expireDays}, total_quantity=#{totalQuantity}, status=#{status} WHERE id=#{id}")
    int update(Coupon coupon);

    // 删除优惠券
    @Delete("DELETE FROM coupon WHERE id = #{id}")
    int deleteById(Integer id);
}