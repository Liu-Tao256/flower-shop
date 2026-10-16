package com.flower.mapper;

import com.flower.entity.CheckinRecord;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface CheckinMapper {

    @Insert("INSERT INTO checkin_record(user_id, plant_id, plant_name, image_url, points, created_at) " +
            "VALUES(#{userId}, #{plantId}, #{plantName}, #{imageUrl}, #{points}, #{createdAt})")
    int insert(CheckinRecord record);

    @Select("SELECT * FROM checkin_record WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<CheckinRecord> findByUserId(Long userId);

    @Select("SELECT COUNT(*) FROM checkin_record WHERE user_id = #{userId} AND DATE(created_at) = #{date}")
    int countByDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Select("SELECT COUNT(*) FROM checkin_record WHERE user_id = #{userId} AND plant_id = #{plantId} AND DATE(created_at) = #{date}")
    int countByUserAndPlantAndDate(@Param("userId") Long userId, @Param("plantId") Integer plantId, @Param("date") LocalDate date);

    @Select("SELECT COUNT(*) FROM checkin_record WHERE user_id = #{userId}")
    int getTotalCheckinCount(Long userId);

    @Select("SELECT COUNT(DISTINCT plant_id) FROM checkin_record WHERE user_id = #{userId}")
    int getDistinctPlantCount(Long userId);

    @Select("SELECT * FROM checkin_record WHERE user_id = #{userId} AND plant_id = #{plantId} ORDER BY created_at DESC LIMIT 1")
    CheckinRecord findLastByPlant(@Param("userId") Long userId, @Param("plantId") Integer plantId);

    @Select("SELECT plant_id, plant_name, COUNT(*) as checkin_count, MAX(created_at) as last_checkin " +
            "FROM checkin_record WHERE user_id = #{userId} " +
            "GROUP BY plant_id, plant_name ORDER BY last_checkin DESC")
    List<Map<String, Object>> getCheckinPlants(@Param("userId") Long userId);
}