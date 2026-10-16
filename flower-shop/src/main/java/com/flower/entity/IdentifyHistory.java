package com.flower.entity;

import java.time.LocalDateTime;

public class IdentifyHistory {
    private Long id;
    private Long userId;
    private Integer plantId;
    private String plantName;
    private String imageUrl;
    private Double confidence;
    private LocalDateTime createdAt;

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Integer getPlantId() { return plantId; }
    public String getPlantName() { return plantName; }
    public String getImageUrl() { return imageUrl; }
    public Double getConfidence() { return confidence; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setPlantId(Integer plantId) { this.plantId = plantId; }
    public void setPlantName(String plantName) { this.plantName = plantName; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}