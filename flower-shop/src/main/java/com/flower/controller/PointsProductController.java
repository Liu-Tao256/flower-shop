package com.flower.controller;

import com.flower.entity.PointsProduct;
import com.flower.mapper.PointsProductMapper;
import com.flower.mapper.UserMapper;
import com.flower.utils.JwtUtil;
import com.flower.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PointsProductController {

    @Autowired
    private PointsProductMapper pointsProductMapper;

    @Autowired
    private UserMapper userMapper;

    private Long getUserId(HttpServletRequest request) {
        return JwtUtil.getUserIdFromRequest(request);
    }

    // 获取积分商品列表
    @GetMapping("/points-products")
    public Result<List<PointsProduct>> getPointsProducts() {
        return Result.success(pointsProductMapper.findAll());
    }

    // 兑换积分商品
    @PostMapping("/exchange/product/{productId}")
    public Result<?> exchangeProduct(@PathVariable Integer productId, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error("未登录");
        }

        PointsProduct product = pointsProductMapper.findById(productId);
        if (product == null) {
            return Result.error("商品不存在");
        }

        if (product.getStock() <= 0) {
            return Result.error("库存不足");
        }

        // 检查用户积分
        int userPoints = userMapper.findPointsByUserId(userId);
        if (userPoints < product.getPoints()) {
            return Result.error("积分不足");
        }

        // 扣减积分
        userMapper.deductPoints(userId, product.getPoints());

        // 扣减库存
        pointsProductMapper.decreaseStock(productId);

        return Result.success("兑换成功，请到店领取");
    }
}