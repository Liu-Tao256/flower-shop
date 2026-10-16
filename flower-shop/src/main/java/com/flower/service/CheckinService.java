package com.flower.service;

import com.flower.entity.CheckinRecord;
import com.flower.mapper.CheckinMapper;
import com.flower.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

@Service
public class CheckinService {

    @Autowired
    private CheckinMapper checkinMapper;

    @Autowired
    private UserMapper userMapper;

    private static final int CHECKIN_POINTS = 10;

    /**
     * 打卡（每个品种每天只能打卡一次）
     * @return true-打卡成功，false-今天已打卡过该植物
     */
    @Transactional
    public boolean checkin(Long userId, Integer plantId, String plantName, String imageUrl) {
        // 检查今天是否已经打卡过该植物
        int todayCount = checkinMapper.countByUserAndPlantAndDate(userId, plantId, LocalDate.now());
        if (todayCount > 0) {
            return false; // 今天已打卡过该植物
        }

        // 保存打卡记录
        CheckinRecord record = new CheckinRecord();
        record.setUserId(userId);
        record.setPlantId(plantId);
        record.setPlantName(plantName);
        record.setImageUrl(imageUrl);
        record.setPoints(CHECKIN_POINTS);
        record.setCreatedAt(LocalDateTime.now());
        checkinMapper.insert(record);

        // 增加用户积分
        userMapper.addPoints(userId, CHECKIN_POINTS);

        return true;
    }

    /**
     * 获取用户所有打卡记录
     */
    public List<CheckinRecord> getCheckinRecords(Long userId) {
        return checkinMapper.findByUserId(userId);
    }

    /**
     * 获取用户总打卡次数
     */
    public int getTotalCheckinCount(Long userId) {
        return checkinMapper.getTotalCheckinCount(userId);
    }

    /**
     * 检查今天是否已打卡
     */
    public boolean hasCheckinToday(Long userId) {
        return checkinMapper.countByDate(userId, LocalDate.now()) > 0;
    }

    /**
     * 检查今天是否已打卡过指定植物
     */
    public boolean hasCheckinTodayForPlant(Long userId, Integer plantId) {
        return checkinMapper.countByUserAndPlantAndDate(userId, plantId, LocalDate.now()) > 0;
    }

    public List<Map<String, Object>> getCheckinPlants(Long userId) {
        return checkinMapper.getCheckinPlants(userId);
    }

    public int getDistinctPlantCount(Long userId) {
        return checkinMapper.getDistinctPlantCount(userId);
    }
}