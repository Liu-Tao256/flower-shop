package com.flower.mapper;

import com.flower.entity.Product;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProductMapper {

    // ========== 用户端方法 ==========

    @Select("SELECT * FROM product WHERE status = 'on' ORDER BY sales_count DESC")
    List<Product> findAll();

    @Select("SELECT * FROM product WHERE id = #{id}")
    Product findById(Integer id);

    @Select("SELECT * FROM product WHERE category_id = #{categoryId} AND status = 'on'")
    List<Product> findByCategory(Integer categoryId);

    // ========== 管理后台方法 ==========

    @Select("SELECT COUNT(*) FROM product")
    int count();

    @Select("SELECT * FROM product ORDER BY id")
    List<Product> findAllForAdmin();

    @Insert("INSERT INTO product(name, category_id, price, original_price, stock, main_image, description, sales_count, created_at, updated_at) " +
            "VALUES(#{name}, #{categoryId}, #{price}, #{originalPrice}, #{stock}, #{mainImage}, #{description}, #{salesCount}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    @Update("UPDATE product SET name=#{name}, price=#{price}, original_price=#{originalPrice}, stock=#{stock}, " +
            "main_image=#{mainImage}, description=#{description}, updated_at=#{updatedAt} WHERE id=#{id}")
    int update(Product product);

    @Update("UPDATE product SET status = #{status} WHERE id = #{id}")
    void updateStatus(@Param("id") Integer id, @Param("status") String status);

    @Delete("DELETE FROM product WHERE id = #{id}")
    int deleteById(Integer id);

    // ========== 销量更新方法 ==========

    @Update("UPDATE product SET sales_count = sales_count + #{quantity} WHERE id = #{productId}")
    void increaseSales(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}