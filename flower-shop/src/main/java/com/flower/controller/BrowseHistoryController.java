package com.flower.controller;

import com.flower.entity.BrowseHistory;
import com.flower.service.BrowseHistoryService;
import com.flower.utils.JwtUtil;
import com.flower.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/browse-history")
public class BrowseHistoryController {

    @Autowired
    private BrowseHistoryService browseHistoryService;

    private Long getUserId(HttpServletRequest request) {
        return JwtUtil.getUserIdFromRequest(request);
    }


    //  保存浏览记录
    @PostMapping
    public Result<?> saveBrowseHistory(@RequestBody BrowseHistory history, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error("未登录");
        }
        history.setUserId(userId);
        history.setCreatedAt(LocalDateTime.now());
        browseHistoryService.saveBrowseHistory(history);
        return Result.success("保存成功");
    }


    //  获取浏览记录列表

    @GetMapping
    public Result<List<BrowseHistory>> getBrowseHistory(HttpServletRequest request,
                                                        @RequestParam(defaultValue = "50") int limit) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error("未登录");
        }
        return Result.success(browseHistoryService.getBrowseHistory(userId, limit));
    }


    // 删除单条浏览记录

    @DeleteMapping("/{id}")
    public Result<?> deleteBrowseHistory(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error("未登录");
        }
        browseHistoryService.deleteBrowseHistory(userId, id);
        return Result.success("删除成功");
    }


     //清空浏览记录

    @DeleteMapping("/clear")
    public Result<?> clearBrowseHistory(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error("未登录");
        }
        browseHistoryService.clearBrowseHistory(userId);
        return Result.success("清空成功");
    }
}