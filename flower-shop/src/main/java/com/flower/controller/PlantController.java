package com.flower.controller;

import com.flower.dto.IdentifyResultDTO;
import com.flower.entity.Plant;
import com.flower.service.PlantService;
import com.flower.utils.JwtUtil;
import com.flower.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/plant")
public class PlantController {

    @Autowired
    private PlantService plantService;

    /**
     * 植物识别接口
     * @param image 上传的植物图片
     * @param request HttpServletRequest（用于获取用户ID）
     * @return 识别结果
     */
    @PostMapping("/identify")
    public Result<IdentifyResultDTO> identify(@RequestParam("image") MultipartFile image, HttpServletRequest request) {
        try {
            Long userId = JwtUtil.getUserIdFromRequest(request);
            String imageUrl = "https://picsum.photos/200/200?random=" + System.currentTimeMillis();
            IdentifyResultDTO result = plantService.identifyPlant(image, userId, imageUrl);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("识别失败：" + e.getMessage());
        }
    }

    /**
     * 获取植物详情
     * @param id 植物ID
     * @return 植物详情
     */
    @GetMapping("/{id}")
    public Result<Plant> getPlantDetail(@PathVariable Integer id) {
        Plant plant = plantService.getPlantDetail(id);
        if (plant == null) {
            return Result.error("植物不存在");
        }
        return Result.success(plant);
    }

    /**
     * 获取植物图鉴列表（支持分类筛选和搜索）
     * @param type 植物类型（flower/green/succulent）
     * @param keyword 搜索关键词
     * @return 植物列表
     */
    @GetMapping("/gallery")
    public Result<List<Plant>> getPlantGallery(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {
        List<Plant> plants = plantService.getPlantGallery(type, keyword);
        return Result.success(plants);
    }

    @GetMapping("/list")
    public Result<Object> getPlantList(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success("功能开发中");
    }

    @PostMapping("/{id}/checkin")
    public Result<?> checkin(@PathVariable Integer id, HttpServletRequest request) {
        return Result.success("打卡成功，+10积分");
    }


    @GetMapping("/top-rank")
    public Result<List<Plant>> getTopRank(@RequestParam(defaultValue = "week") String period) {
        List<Plant> plants = plantService.getTopPlants(10);
        return Result.success(plants);
    }
}