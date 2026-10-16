package com.flower.entity;

import java.time.LocalDateTime;

public class Address {
    private Long id;
    private Long userId;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detail;
    private Integer isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getReceiverName() { return receiverName; }
    public String getReceiverPhone() { return receiverPhone; }
    public String getProvince() { return province; }
    public String getCity() { return city; }
    public String getDistrict() { return district; }
    public String getDetail() { return detail; }
    public Integer getIsDefault() { return isDefault; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }
    public void setProvince(String province) { this.province = province; }
    public void setCity(String city) { this.city = city; }
    public void setDistrict(String district) { this.district = district; }
    public void setDetail(String detail) { this.detail = detail; }
    public void setIsDefault(Integer isDefault) { this.isDefault = isDefault; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}