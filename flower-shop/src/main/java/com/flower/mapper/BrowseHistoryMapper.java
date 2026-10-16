package com.flower.mapper;

import com.flower.entity.BrowseHistory;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface BrowseHistoryMapper {

    @Insert("INSERT INTO browse_history(user_id, product_id, plant_id, type, title, image_url, created_at) " +
            "VALUES(#{userId}, #{productId}, #{plantId}, #{type}, #{title}, #{imageUrl}, #{createdAt})")
    int insert(BrowseHistory history);

    @Select("SELECT * FROM browse_history WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{limit}")
    List<BrowseHistory> findByUserId(@Param("userId") Long userId, @Param("limit") int limit);

    @Delete("DELETE FROM browse_history WHERE user_id = #{userId} AND id = #{id}")
    int deleteById(@Param("userId") Long userId, @Param("id") Long id);

    @Delete("DELETE FROM browse_history WHERE user_id = #{userId}")
    int clearByUserId(Long userId);
}