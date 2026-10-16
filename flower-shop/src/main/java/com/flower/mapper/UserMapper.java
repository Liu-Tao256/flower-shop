package com.flower.mapper;

import com.flower.entity.User;
import com.flower.entity.UserTask;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE phone = #{phone}")
    User findByPhone(String phone);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    @Select("SELECT * FROM user")
    List<User> findAll();

    @Select("SELECT COUNT(*) FROM user")
    int count();

    @Insert("INSERT INTO user(phone, nickname, avatar, password, role, status, points, created_at, updated_at) " +
            "VALUES(#{phone}, #{nickname}, #{avatar}, #{password}, #{role}, #{status}, #{points}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE user SET nickname=#{nickname}, avatar=#{avatar}, updated_at=#{updatedAt} WHERE id=#{id}")
    int updateProfile(User user);

    @Update("UPDATE user SET password=#{password}, updated_at=#{updatedAt} WHERE id=#{id}")
    int updatePassword(User user);

    @Update("UPDATE user SET phone=#{phone}, updated_at=#{updatedAt} WHERE id=#{id}")
    int updatePhone(User user);

    @Delete("DELETE FROM user WHERE id=#{id}")
    int deleteById(Long id);

    // 增加积分
    @Update("UPDATE user SET points = points + #{points} WHERE id = #{userId}")
    void addPoints(@Param("userId") Long userId, @Param("points") int points);

    @Update("UPDATE user_task SET progress = #{progress}, completed = #{completed}, updated_at = NOW() WHERE id = #{id}")
    int update(UserTask userTask);

    @Select("SELECT points FROM user WHERE id = #{userId}")
    int findPointsByUserId(Long userId);

    @Update("UPDATE user SET points = points - #{points} WHERE id = #{userId}")
    void deductPoints(@Param("userId") Long userId, @Param("points") int points);
}