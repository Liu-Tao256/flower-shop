package com.flower.service;

import com.flower.entity.IdentifyHistory;
import com.flower.mapper.HistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistoryService {

    @Autowired
    private HistoryMapper historyMapper;

    public void saveHistory(Long userId, Integer plantId, String plantName, Double confidence, String imageUrl) {
        IdentifyHistory history = new IdentifyHistory();
        history.setUserId(userId);
        history.setPlantId(plantId);
        history.setPlantName(plantName);
        history.setConfidence(confidence);
        history.setImageUrl(imageUrl);
        history.setCreatedAt(LocalDateTime.now());
        historyMapper.insert(history);
    }

    public List<IdentifyHistory> getHistoryByUserId(Long userId) {
        return historyMapper.findByUserId(userId);
    }

    public void deleteHistory(Long id) {
        historyMapper.deleteById(id);
    }
}