package com.flower.mapper;

import com.flower.entity.PointsProduct;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PointsProductMapper {

    // 用户端：只查询上架的商品
    @Select("SELECT * FROM points_product WHERE status = 1 ORDER BY points")
    List<PointsProduct> findAll();

    // 管理后台：查询所有商品
    @Select("SELECT * FROM points_product ORDER BY points")
    List<PointsProduct> findAllForAdmin();

    @Select("SELECT * FROM points_product WHERE id = #{id}")
    PointsProduct findById(Integer id);

    @Insert("INSERT INTO points_product(name, points, image_url, stock, status) " +
            "VALUES(#{name}, #{points}, #{imageUrl}, #{stock}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PointsProduct product);

    @Update("UPDATE points_product SET name=#{name}, points=#{points}, " +
            "image_url=#{imageUrl}, stock=#{stock}, status=#{status} WHERE id=#{id}")
    int update(PointsProduct product);

    @Delete("DELETE FROM points_product WHERE id = #{id}")
    int deleteById(Integer id);

    @Update("UPDATE points_product SET stock = stock - 1 WHERE id = #{id} AND stock > 0")
    int decreaseStock(Integer id);
}