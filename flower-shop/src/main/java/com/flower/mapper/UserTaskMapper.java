package com.flower.mapper;

import com.flower.entity.UserTask;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserTaskMapper {

    @Insert("INSERT INTO user_task(user_id, task_id, progress, completed, reward_received) " +
            "VALUES(#{userId}, #{taskId}, #{progress}, #{completed}, #{rewardReceived})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserTask userTask);

    @Update("UPDATE user_task SET progress = #{progress}, completed = #{completed}, reward_received = #{rewardReceived}, updated_at = NOW() WHERE id = #{id}")
    int update(UserTask userTask);

    @Select("SELECT * FROM user_task WHERE user_id = #{userId}")
    List<UserTask> findByUserId(Long userId);

    @Select("SELECT * FROM user_task WHERE user_id = #{userId} AND task_id = #{taskId}")
    UserTask findByUserAndTask(@Param("userId") Long userId, @Param("taskId") Integer taskId);
}