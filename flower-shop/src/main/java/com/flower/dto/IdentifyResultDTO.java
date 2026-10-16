package com.flower.dto;

import java.util.List;

public class IdentifyResultDTO {
    private Integer plantId;
    private String name;
    private String alias;
    private String family;
    private String genus;
    private String type;
    private String characteristic;
    private String careKnowledge;
    private String imageUrl;
    private Double confidence;
    private List<ProductDTO> relatedProducts;

    // Getters and Setters
    public Integer getPlantId() { return plantId; }
    public void setPlantId(Integer plantId) { this.plantId = plantId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public String getFamily() { return family; }
    public void setFamily(String family) { this.family = family; }

    public String getGenus() { return genus; }
    public void setGenus(String genus) { this.genus = genus; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCharacteristic() { return characteristic; }
    public void setCharacteristic(String characteristic) { this.characteristic = characteristic; }

    public String getCareKnowledge() { return careKnowledge; }
    public void setCareKnowledge(String careKnowledge) { this.careKnowledge = careKnowledge; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public List<ProductDTO> getRelatedProducts() { return relatedProducts; }
    public void setRelatedProducts(List<ProductDTO> relatedProducts) { this.relatedProducts = relatedProducts; }

    public static class ProductDTO {
        private Integer id;
        private String name;
        private Double price;
        private String mainImage;

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        public String getMainImage() { return mainImage; }
        public void setMainImage(String mainImage) { this.mainImage = mainImage; }
    }
}