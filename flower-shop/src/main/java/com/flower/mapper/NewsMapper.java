package com.flower.mapper;

import com.flower.entity.News;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface NewsMapper {

    @Select("SELECT * FROM news WHERE status = 1 ORDER BY sort_order, created_at DESC")
    List<News> findAll();
}