package com.flower.mapper;

import com.flower.entity.IdentifyHistory;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface HistoryMapper {

    @Insert("INSERT INTO identify_history(user_id, plant_id, plant_name, image_url, confidence, created_at) " +
            "VALUES(#{userId}, #{plantId}, #{plantName}, #{imageUrl}, #{confidence}, #{createdAt})")
    int insert(IdentifyHistory history);

    @Select("SELECT * FROM identify_history WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<IdentifyHistory> findByUserId(Long userId);

    @Delete("DELETE FROM identify_history WHERE id = #{id}")
    int deleteById(Long id);
}