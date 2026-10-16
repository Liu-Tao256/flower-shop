package com.flower.controller;

import com.flower.service.CheckinService;
import com.flower.utils.JwtUtil;
import com.flower.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/checkin")
public class CheckinController {

    @Autowired
    private CheckinService checkinService;

    private Long getUserId(HttpServletRequest request) {
        return JwtUtil.getUserIdFromRequest(request);
    }

    @PostMapping
    public Result<?> checkin(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error("未登录");
        }

        Integer plantId = params.get("plantId") != null ? (Integer) params.get("plantId") : null;
        String plantName = (String) params.get("plantName");
        String imageUrl = (String) params.get("imageUrl");

        boolean success = checkinService.checkin(userId, plantId, plantName, imageUrl);
        if (success) {
            Map<String, Object> result = new HashMap<>();
            result.put("points", 10);
            result.put("message", "打卡成功，获得10积分");
            return Result.success(result);
        } else {
            return Result.error("今天已经打卡过了");
        }
    }

    @GetMapping("/status")
    public Result<Map<String, Object>> getCheckinStatus(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error("未登录");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("hasCheckinToday", checkinService.hasCheckinToday(userId));
        result.put("totalCount", checkinService.getTotalCheckinCount(userId));
        return Result.success(result);
    }

    @GetMapping("/records")
    public Result<?> getCheckinRecords(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error("未登录");
        }
        return Result.success(checkinService.getCheckinRecords(userId));
    }

    @GetMapping("/plants")
    public Result<?> getCheckinPlants(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error("未登录");
        }
        return Result.success(checkinService.getCheckinPlants(userId));
    }
}