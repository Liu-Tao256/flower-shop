package com.flower.mapper;

import com.flower.entity.Order;
import com.flower.entity.OrderItem;
import org.apache.ibatis.annotations.*;
import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface OrderMapper {

    @Insert("INSERT INTO orders(order_no, user_id, address_id, total_amount, delivery_fee, final_amount, payment_method, remark, status, created_at, updated_at) " +
            "VALUES(#{orderNo}, #{userId}, #{addressId}, #{totalAmount}, #{deliveryFee}, #{finalAmount}, #{paymentMethod}, #{remark}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    @Insert("INSERT INTO order_item(order_id, product_id, product_name, price, quantity, total_price) " +
            "VALUES(#{orderId}, #{productId}, #{productName}, #{price}, #{quantity}, #{totalPrice})")
    int insertItem(OrderItem item);

    @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Order> findByUserId(Long userId);

    @Select("SELECT * FROM orders WHERE id = #{id}")
    Order findById(Long id);

    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    List<OrderItem> findItemsByOrderId(Long orderId);

    @Update("UPDATE orders SET status = #{status}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateStatus(Order order);

    @Select("SELECT COUNT(*) FROM orders")
    int count();

    @Select("SELECT SUM(final_amount) FROM orders WHERE status = 3")
    BigDecimal getTotalSales();

    @Select("SELECT * FROM orders ORDER BY created_at DESC LIMIT #{limit}")
    List<Order> findRecent(int limit);

    @Select("<script>" +
            "SELECT * FROM orders WHERE 1=1" +
            "<if test='keyword != null and keyword != \"\"'> AND order_no LIKE CONCAT('%', #{keyword}, '%')</if>" +
            "<if test='status != null'> AND status = #{status}</if>" +
            " ORDER BY created_at DESC" +
            "</script>")
    List<Order> findAll(@Param("keyword") String keyword, @Param("status") Integer status);

    @Update("UPDATE orders SET status = #{status} WHERE id = #{id}")
    void updateStatusById(@Param("id") Long id, @Param("status") Integer status);

    // ========== 订单趋势方法 ==========
    @Select("SELECT COUNT(*) FROM orders WHERE DATE(created_at) = #{date}")
    int countByDate(@Param("date") String date);
}