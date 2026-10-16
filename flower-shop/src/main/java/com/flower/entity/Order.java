package com.flower.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private Long id;
    private String orderNo;
    private Long userId;
    private Long addressId;
    private BigDecimal totalAmount;
    private BigDecimal deliveryFee;
    private BigDecimal finalAmount;
    private String paymentMethod;
    private String remark;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItem> items;  // 新增

    // Getters
    public Long getId() { return id; }
    public String getOrderNo() { return orderNo; }
    public Long getUserId() { return userId; }
    public Long getAddressId() { return addressId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BigDecimal getDeliveryFee() { return deliveryFee; }
    public BigDecimal getFinalAmount() { return finalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getRemark() { return remark; }
    public Integer getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<OrderItem> getItems() { return items; }  // 新增

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setDeliveryFee(BigDecimal deliveryFee) { this.deliveryFee = deliveryFee; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setRemark(String remark) { this.remark = remark; }
    public void setStatus(Integer status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setItems(List<OrderItem> items) { this.items = items; }  // 新增
}