package com.flower.mapper;

import com.flower.entity.Address;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AddressMapper {

    @Select("SELECT * FROM address WHERE user_id = #{userId} ORDER BY is_default DESC, created_at DESC")
    List<Address> findByUserId(Long userId);

    @Select("SELECT * FROM address WHERE id = #{id}")
    Address findById(Long id);

    @Select("SELECT * FROM address WHERE user_id = #{userId} AND is_default = 1")
    Address findDefaultByUserId(Long userId);

    @Insert("INSERT INTO address(user_id, receiver_name, receiver_phone, province, city, district, detail, is_default, created_at, updated_at) " +
            "VALUES(#{userId}, #{receiverName}, #{receiverPhone}, #{province}, #{city}, #{district}, #{detail}, #{isDefault}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Address address);

    @Update("UPDATE address SET receiver_name=#{receiverName}, receiver_phone=#{receiverPhone}, " +
            "province=#{province}, city=#{city}, district=#{district}, detail=#{detail}, updated_at=#{updatedAt} WHERE id=#{id}")
    int update(Address address);

    @Update("UPDATE address SET is_default = 0 WHERE user_id = #{userId}")
    void clearDefault(Long userId);

    @Update("UPDATE address SET is_default = 1 WHERE id = #{id}")
    void setDefault(Long id);

    @Delete("DELETE FROM address WHERE id = #{id}")
    int deleteById(Long id);
}