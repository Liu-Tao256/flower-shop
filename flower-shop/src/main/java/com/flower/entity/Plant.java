package com.flower.entity;

import java.time.LocalDateTime;
import java.util.List;

public class Plant {
    private Integer id;
    private String name;
    private String alias;
    private String family;
    private String genus;
    private String type;
    private String characteristic;
    private String careKnowledge;
    private String imageUrl;
    private String nameEn;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 关联商品（非数据库字段）
    private List<Product> relatedProducts;

    // Getters
    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getAlias() { return alias; }
    public String getFamily() { return family; }
    public String getGenus() { return genus; }
    public String getType() { return type; }
    public String getCharacteristic() { return characteristic; }
    public String getCareKnowledge() { return careKnowledge; }
    public String getImageUrl() { return imageUrl; }
    public String getNameEn() { return nameEn; }
    public Integer getViewCount() { return viewCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<Product> getRelatedProducts() { return relatedProducts; }

    // Setters
    public void setId(Integer id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAlias(String alias) { this.alias = alias; }
    public void setFamily(String family) { this.family = family; }
    public void setGenus(String genus) { this.genus = genus; }
    public void setType(String type) { this.type = type; }
    public void setCharacteristic(String characteristic) { this.characteristic = characteristic; }
    public void setCareKnowledge(String careKnowledge) { this.careKnowledge = careKnowledge; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setRelatedProducts(List<Product> relatedProducts) { this.relatedProducts = relatedProducts; }
}