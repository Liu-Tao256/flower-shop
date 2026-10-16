package com.flower.service;

import com.flower.entity.News;
import com.flower.mapper.NewsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NewsService {

    @Autowired
    private NewsMapper newsMapper;

    public List<News> getAllNews() {
        return newsMapper.findAll();
    }
}