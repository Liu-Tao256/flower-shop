package com.flower.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.flower.dto.IdentifyResultDTO;
import com.flower.entity.Plant;
import com.flower.entity.Product;
import com.flower.mapper.PlantMapper;
import com.flower.mapper.HistoryMapper;
import com.flower.entity.IdentifyHistory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PlantService {

    @Autowired
    private PlantMapper plantMapper;

    @Autowired
    private HistoryMapper historyMapper;

    @Value("${plantnet.api.key:}")
    private String apiKey;

    private static final String API_URL = "https://my-api.plantnet.org/v2/identify/all";

    public IdentifyResultDTO identifyPlant(MultipartFile image, Long userId, String imageUrl) throws IOException {
        String result = callPlantNetAPI(image);
        JSONObject json = JSONObject.parseObject(result);

        if (json.containsKey("error") || json.containsKey("statusCode")) {
            String errorMsg = json.containsKey("message") ? json.getString("message") : json.getString("error");
            throw new RuntimeException("API错误: " + errorMsg);
        }

        if (json.containsKey("results") && json.getJSONArray("results") != null && json.getJSONArray("results").size() > 0) {
            JSONArray results = json.getJSONArray("results");
            JSONObject bestMatch = results.getJSONObject(0);
            JSONObject species = bestMatch.getJSONObject("species");

            String plantName = null;
            if (species.containsKey("commonNames") && species.getJSONArray("commonNames").size() > 0) {
                plantName = species.getJSONArray("commonNames").getString(0);
            }
            if (plantName == null && species.containsKey("scientificNameWithoutAuthor")) {
                plantName = species.getString("scientificNameWithoutAuthor");
            }
            if (plantName == null && species.containsKey("scientificName")) {
                plantName = species.getString("scientificName");
            }
            if (plantName == null) {
                plantName = "未知植物";
            }

            // 英文名映射为中文名
            String lowerName = plantName.toLowerCase();
            if (lowerName.contains("rose")) {
                plantName = "玫瑰";
            } else if (lowerName.contains("lily")) {
                plantName = "百合";
            } else if (lowerName.contains("sunflower")) {
                plantName = "向日葵";
            } else if (lowerName.contains("radish") || lowerName.contains("epipremnum") || lowerName.contains("pothos")) {
                plantName = "绿萝";
            } else if (lowerName.contains("chestnut") || lowerName.contains("malabar")) {
                plantName = "发财树";
            } else if (lowerName.contains("stone") || lowerName.contains("lithops")) {
                plantName = "生石花";
            } else if (lowerName.contains("bear") || lowerName.contains("paw")) {
                plantName = "熊童子";
            } else if (lowerName.contains("lavender")) {
                plantName = "薰衣草";
            } else if (lowerName.contains("jasmine")) {
                plantName = "茉莉花";
            } else if (lowerName.contains("cactus")) {
                plantName = "仙人掌";
            } else if (lowerName.contains("spider")) {
                plantName = "吊兰";
            }
            else if (lowerName.contains("daisy") || lowerName.contains("bellis")) {
                plantName = "雏菊";
            }

            Double score = bestMatch.getDouble("score");
            Plant plant = plantMapper.findByName(plantName);

            IdentifyResultDTO resultDTO = new IdentifyResultDTO();
            resultDTO.setConfidence(score);
            resultDTO.setName(plantName);

            if (plant != null) {
                plantMapper.incrementViewCount(plant.getId());

                resultDTO.setPlantId(plant.getId());
                resultDTO.setAlias(plant.getAlias());
                resultDTO.setFamily(plant.getFamily());
                resultDTO.setGenus(plant.getGenus());
                resultDTO.setType(plant.getType());
                resultDTO.setCharacteristic(plant.getCharacteristic());
                resultDTO.setCareKnowledge(plant.getCareKnowledge());

                List<Product> products = plantMapper.findRelatedProducts(plant.getId());
                List<IdentifyResultDTO.ProductDTO> productList = new ArrayList<>();
                for (Product p : products) {
                    IdentifyResultDTO.ProductDTO dto = new IdentifyResultDTO.ProductDTO();
                    dto.setId(p.getId());
                    dto.setName(p.getName());
                    dto.setPrice(p.getPrice().doubleValue());
                    dto.setMainImage(p.getMainImage());
                    productList.add(dto);
                }
                resultDTO.setRelatedProducts(productList);
            }

            // 保存识别历史
            if (userId != null) {
                saveHistory(userId, plant != null ? plant.getId() : null, plantName, score, imageUrl);
            }

            return resultDTO;
        } else {
            throw new RuntimeException("未识别到植物，请尝试更清晰的图片");
        }
    }

    private void saveHistory(Long userId, Integer plantId, String plantName, Double confidence, String imageUrl) {
        try {
            IdentifyHistory history = new IdentifyHistory();
            history.setUserId(userId);
            history.setPlantId(plantId);
            history.setPlantName(plantName);
            history.setConfidence(confidence);
            history.setImageUrl(imageUrl);
            history.setCreatedAt(LocalDateTime.now());
            historyMapper.insert(history);
        } catch (Exception e) {
            System.err.println("保存识别历史失败: " + e.getMessage());
        }
    }

    private String callPlantNetAPI(MultipartFile image) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = API_URL + "?api-key=" + apiKey;
            HttpPost post = new HttpPost(url);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("images", image.getBytes(),
                    ContentType.create(image.getContentType()), image.getOriginalFilename());
            builder.addTextBody("organs", "leaf", ContentType.TEXT_PLAIN);

            post.setEntity(builder.build());

            try (CloseableHttpResponse response = client.execute(post)) {
                return EntityUtils.toString(response.getEntity());
            }
        }
    }

    public Plant getPlantDetail(Integer id) {
        plantMapper.incrementViewCount(id);
        Plant plant = plantMapper.findById(id);
        if (plant != null) {
            plant.setRelatedProducts(plantMapper.findRelatedProducts(id));
        }
        return plant;
    }

    // ========== 植物图鉴方法 ==========
    public List<Plant> getPlantGallery(String type, String keyword) {
        return plantMapper.findGallery(type, keyword);
    }

    // ========== 管理后台方法 ==========
    public List<Plant> getAllPlants() {
        return plantMapper.findAll();
    }

    public void addPlant(Plant plant) {
        plant.setCreatedAt(LocalDateTime.now());
        plant.setUpdatedAt(LocalDateTime.now());
        plantMapper.insert(plant);
    }

    public void updatePlant(Plant plant) {
        plant.setUpdatedAt(LocalDateTime.now());
        plantMapper.update(plant);
    }

    public void deletePlant(Integer id) {
        plantMapper.deleteById(id);
    }

    public List<Plant> getTopPlants(int limit) {
        return plantMapper.findTop(limit);
    }

    public void updatePlantRelations(Integer plantId, List<Integer> productIds) {
        // 先删除原有关联
        plantMapper.deleteRelations(plantId);
        // 添加新关联
        if (productIds != null && !productIds.isEmpty()) {
            for (Integer productId : productIds) {
                plantMapper.addRelation(plantId, productId);
            }
        }
    }

    public List<Plant> searchPlants(String keyword) {
        return plantMapper.searchByName(keyword);
    }
}