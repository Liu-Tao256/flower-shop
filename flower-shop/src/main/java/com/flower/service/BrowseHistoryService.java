package com.flower.service;

import com.flower.entity.BrowseHistory;
import com.flower.mapper.BrowseHistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BrowseHistoryService {

    @Autowired
    private BrowseHistoryMapper browseHistoryMapper;


     // 保存浏览记录

    public void saveBrowseHistory(BrowseHistory history) {
        // 可选：删除同类型同内容的旧记录（保持最新）
        // browseHistoryMapper.deleteDuplicate(history.getUserId(), history.getProductId(), history.getPlantId());
        browseHistoryMapper.insert(history);
    }


     // 获取用户浏览记录

    public List<BrowseHistory> getBrowseHistory(Long userId, int limit) {
        return browseHistoryMapper.findByUserId(userId, limit);
    }


    // 删除单条浏览记录

    public void deleteBrowseHistory(Long userId, Long id) {
        browseHistoryMapper.deleteById(userId, id);
    }


    // 清空用户浏览记录

    public void clearBrowseHistory(Long userId) {
        browseHistoryMapper.clearByUserId(userId);
    }
}