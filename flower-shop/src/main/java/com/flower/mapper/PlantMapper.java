package com.flower.mapper;

import com.flower.entity.Plant;
import com.flower.entity.Product;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PlantMapper {

    /**
     * 根据名称查询植物（支持中文名、英文名、别名）
     */
    @Select("SELECT p.* FROM plant p LEFT JOIN plant_alias a ON p.id = a.plant_id " +
            "WHERE p.name = #{name} OR p.name_en = #{name} OR a.alias_en = #{name} " +
            "LIMIT 1")
    Plant findByName(String name);

    @Select("SELECT * FROM plant WHERE id = #{id}")
    Plant findById(Integer id);

    @Select("SELECT * FROM plant ORDER BY id")
    List<Plant> findAll();

    @Select("SELECT * FROM plant WHERE type = #{type} ORDER BY id")
    List<Plant> findByType(String type);

    @Select("SELECT p.* FROM product p JOIN plant_product_rel r ON p.id = r.product_id WHERE r.plant_id = #{plantId}")
    List<Product> findRelatedProducts(Integer plantId);

    @Insert("INSERT INTO plant(name, alias, family, genus, type, characteristic, care_knowledge, image_url, name_en, created_at, updated_at) " +
            "VALUES(#{name}, #{alias}, #{family}, #{genus}, #{type}, #{characteristic}, #{careKnowledge}, #{imageUrl}, #{nameEn}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Plant plant);

    @Update("UPDATE plant SET name=#{name}, alias=#{alias}, family=#{family}, genus=#{genus}, type=#{type}, " +
            "characteristic=#{characteristic}, care_knowledge=#{careKnowledge}, image_url=#{imageUrl}, " +
            "name_en=#{nameEn}, updated_at=#{updatedAt} WHERE id=#{id}")
    int update(Plant plant);

    @Delete("DELETE FROM plant WHERE id = #{id}")
    int deleteById(Integer id);

    @Update("UPDATE plant SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(Integer id);

    // ========== 植物图鉴方法 ==========
    @Select("<script>" +
            "SELECT * FROM plant WHERE 1=1" +
            "<if test='type != null and type != \"\"'> AND type = #{type}</if>" +
            "<if test='keyword != null and keyword != \"\"'> AND (name LIKE CONCAT('%', #{keyword}, '%') OR name_en LIKE CONCAT('%', #{keyword}, '%'))</if>" +
            " ORDER BY id" +
            "</script>")
    List<Plant> findGallery(@Param("type") String type, @Param("keyword") String keyword);

    // ========== 管理后台方法 ==========
    @Select("SELECT * FROM plant ORDER BY view_count DESC LIMIT #{limit}")
    List<Plant> findTop(int limit);

    @Delete("DELETE FROM plant_product_rel WHERE plant_id = #{plantId}")
    void deleteRelations(Integer plantId);

    @Insert("INSERT INTO plant_product_rel(plant_id, product_id) VALUES(#{plantId}, #{productId})")
    void addRelation(@Param("plantId") Integer plantId, @Param("productId") Integer productId);

    @Select("SELECT * FROM plant WHERE name LIKE CONCAT('%', #{keyword}, '%') OR name_en LIKE CONCAT('%', #{keyword}, '%')")
    List<Plant> searchByName(String keyword);

    // ========== 别名表方法 ==========
    @Insert("INSERT INTO plant_alias(plant_id, alias_en) VALUES(#{plantId}, #{aliasEn})")
    void addAlias(@Param("plantId") Integer plantId, @Param("aliasEn") String aliasEn);

    @Select("SELECT alias_en FROM plant_alias WHERE plant_id = #{plantId}")
    List<String> findAliasesByPlantId(Integer plantId);

    @Delete("DELETE FROM plant_alias WHERE plant_id = #{plantId}")
    void deleteAliases(Integer plantId);
}