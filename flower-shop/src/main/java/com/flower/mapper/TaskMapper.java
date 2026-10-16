package com.flower.mapper;

import com.flower.entity.Task;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface TaskMapper {

    // 查询所有任务

    @Select("SELECT * FROM task ORDER BY id")
    List<Task> findAll();


    // 查询启用的任务

    @Select("SELECT * FROM task WHERE status = 1 ORDER BY id")
    List<Task> findActive();


    // 根据ID查询任务

    @Select("SELECT * FROM task WHERE id = #{id}")
    Task findById(Integer id);


    // 新增任务

    @Insert("INSERT INTO task(name, description, type, target_value, reward_points, reward_coupon, status) " +
            "VALUES(#{name}, #{description}, #{type}, #{targetValue}, #{rewardPoints}, #{rewardCoupon}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Task task);


    // 修改任务

    @Update("UPDATE task SET name = #{name}, description = #{description}, type = #{type}, " +
            "target_value = #{targetValue}, reward_points = #{rewardPoints}, reward_coupon = #{rewardCoupon}, " +
            "status = #{status} WHERE id = #{id}")
    int update(Task task);


     // 删除任务

    @Delete("DELETE FROM task WHERE id = #{id}")
    int deleteById(Integer id);
}