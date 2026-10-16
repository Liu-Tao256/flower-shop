package com.flower.entity;

import java.time.LocalDateTime;

public class Task {
    private Integer id;
    private String name;
    private String description;
    private String type;
    private Integer targetValue;
    private Integer rewardPoints;
    private String rewardCoupon;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 非数据库字段（用于前端展示）
    private Integer progress;
    private Integer completed;
    private Integer rewardReceived;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getTargetValue() { return targetValue; }
    public void setTargetValue(Integer targetValue) { this.targetValue = targetValue; }

    public Integer getRewardPoints() { return rewardPoints; }
    public void setRewardPoints(Integer rewardPoints) { this.rewardPoints = rewardPoints; }

    public String getRewardCoupon() { return rewardCoupon; }
    public void setRewardCoupon(String rewardCoupon) { this.rewardCoupon = rewardCoupon; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }

    public Integer getCompleted() { return completed; }
    public void setCompleted(Integer completed) { this.completed = completed; }

    public Integer getRewardReceived() { return rewardReceived; }
    public void setRewardReceived(Integer rewardReceived) { this.rewardReceived = rewardReceived; }
}