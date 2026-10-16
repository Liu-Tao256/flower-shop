package com.flower.service;

import com.flower.entity.Task;
import com.flower.entity.UserTask;
import com.flower.mapper.TaskMapper;
import com.flower.mapper.UserTaskMapper;
import com.flower.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private UserTaskMapper userTaskMapper;

    @Autowired
    private UserMapper userMapper;

    // ========== 用户端方法 ==========


     //获取用户任务列表（含进度和完成状态）

    public List<Task> getUserTasksWithStatus(Long userId) {
        List<Task> allTasks = taskMapper.findActive();
        List<UserTask> userTasks = userTaskMapper.findByUserId(userId);

        for (Task task : allTasks) {
            task.setProgress(0);
            task.setCompleted(0);
            task.setRewardReceived(0);

            for (UserTask ut : userTasks) {
                if (ut.getTaskId().equals(task.getId())) {
                    task.setProgress(ut.getProgress());
                    task.setCompleted(ut.getCompleted());
                    task.setRewardReceived(ut.getRewardReceived());
                    break;
                }
            }
        }
        return allTasks;
    }


     // 更新用户任务进度

    @Transactional
    public void updateUserTaskProgress(Long userId, String type, int currentValue) {
        List<Task> tasks = taskMapper.findActive();
        for (Task task : tasks) {
            if (task.getType().equals(type)) {
                UserTask userTask = userTaskMapper.findByUserAndTask(userId, task.getId());

                int progress = Math.min(currentValue, task.getTargetValue());

                if (userTask == null) {
                    userTask = new UserTask();
                    userTask.setUserId(userId);
                    userTask.setTaskId(task.getId());
                    userTask.setProgress(progress);
                    userTask.setCompleted(progress >= task.getTargetValue() ? 1 : 0);
                    userTask.setRewardReceived(0);
                    userTaskMapper.insert(userTask);
                } else if (userTask.getCompleted() == 0) {
                    userTask.setProgress(progress);
                    if (progress >= task.getTargetValue()) {
                        userTask.setCompleted(1);
                    }
                    userTaskMapper.update(userTask);
                }
            }
        }
    }


    // 领取任务奖励

    @Transactional
    public void claimReward(Long userId, Integer taskId) {
        UserTask userTask = userTaskMapper.findByUserAndTask(userId, taskId);
        if (userTask != null && userTask.getCompleted() == 1 && userTask.getRewardReceived() == 0) {
            Task task = taskMapper.findById(taskId);
            if (task != null && task.getRewardPoints() != null && task.getRewardPoints() > 0) {
                // 增加用户积分
                userMapper.addPoints(userId, task.getRewardPoints());
            }
            userTask.setRewardReceived(1);
            userTaskMapper.update(userTask);
        }
    }

    // ========== 管理后台方法 ==========

    public List<Task> getAllTasks() {
        return taskMapper.findAll();
    }

    public Task getTaskById(Integer id) {
        return taskMapper.findById(id);
    }

    public void addTask(Task task) {
        taskMapper.insert(task);
    }

    public void updateTask(Task task) {
        taskMapper.update(task);
    }

    public void deleteTask(Integer id) {
        taskMapper.deleteById(id);
    }
}