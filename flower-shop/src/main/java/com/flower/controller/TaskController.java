package com.flower.controller;

import com.flower.entity.Task;
import com.flower.service.CheckinService;
import com.flower.service.TaskService;
import com.flower.utils.JwtUtil;
import com.flower.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private CheckinService checkinService;

    private Long getUserId(HttpServletRequest request) {
        return JwtUtil.getUserIdFromRequest(request);
    }

    // ========== 用户端接口 ==========


     // 获取用户任务列表（含进度）

    @GetMapping
    public Result<List<Task>> getUserTasks(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error("未登录");
        }
        return Result.success(taskService.getUserTasksWithStatus(userId));
    }


     // 领取任务奖励

    @PostMapping("/{taskId}/claim")
    public Result<?> claimReward(@PathVariable Integer taskId, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error("未登录");
        }
        taskService.claimReward(userId, taskId);
        return Result.success("领取成功");
    }

    //     更新任务进度（打卡后调用）

    @PostMapping("/update-progress")
    public Result<?> updateProgress(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error("未登录");
        }

        // 获取用户总打卡次数
        int totalCheckinCount = checkinService.getTotalCheckinCount(userId);
        // 获取用户识别过的不同植物数量
        int distinctPlantCount = checkinService.getDistinctPlantCount(userId);

        // 更新打卡次数类任务
        taskService.updateUserTaskProgress(userId, "checkin_count", totalCheckinCount);
        // 更新识别植物类任务
        taskService.updateUserTaskProgress(userId, "plant_count", distinctPlantCount);

        return Result.success("更新成功");
    }

    // ========== 管理后台接口 ==========


     // 获取所有任务（管理后台）

    @GetMapping("/admin")
    public Result<List<Task>> getAllTasks() {
        return Result.success(taskService.getAllTasks());
    }


     // 获取任务详情

    @GetMapping("/admin/{id}")
    public Result<Task> getTaskById(@PathVariable Integer id) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return Result.error("任务不存在");
        }
        return Result.success(task);
    }


     // 新增任务

    @PostMapping("/admin")
    public Result<?> addTask(@RequestBody Task task) {
        try {
            taskService.addTask(task);
            return Result.success("添加成功");
        } catch (Exception e) {
            return Result.error("添加失败：" + e.getMessage());
        }
    }


     // 修改任务

    @PutMapping("/admin/{id}")
    public Result<?> updateTask(@PathVariable Integer id, @RequestBody Task task) {
        try {
            task.setId(id);
            taskService.updateTask(task);
            return Result.success("修改成功");
        } catch (Exception e) {
            return Result.error("修改失败：" + e.getMessage());
        }
    }


    //  删除任务

    @DeleteMapping("/admin/{id}")
    public Result<?> deleteTask(@PathVariable Integer id) {
        try {
            taskService.deleteTask(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}