package com.flower.controller;

import com.flower.entity.IdentifyHistory;
import com.flower.service.HistoryService;
import com.flower.utils.JwtUtil;
import com.flower.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    private Long getUserId(HttpServletRequest request) {
        return JwtUtil.getUserIdFromRequest(request);
    }

    @GetMapping
    public Result<List<IdentifyHistory>> getHistory(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error("未登录");
        }
        return Result.success(historyService.getHistoryByUserId(userId));
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteHistory(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error("未登录");
        }
        historyService.deleteHistory(id);
        return Result.success("删除成功");
    }
}