package com.flower.service;

import com.flower.entity.PointsProduct;
import com.flower.mapper.PointsProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PointsProductService {

    @Autowired
    private PointsProductMapper pointsProductMapper;

    // 用户端获取商品
    public List<PointsProduct> getAllProducts() {
        return pointsProductMapper.findAll();
    }

    // 管理后台获取所有商品
    public List<PointsProduct> getAllProductsForAdmin() {
        return pointsProductMapper.findAllForAdmin();
    }

    public PointsProduct getProductById(Integer id) {
        return pointsProductMapper.findById(id);
    }

    public void addProduct(PointsProduct product) {
        pointsProductMapper.insert(product);
    }

    public void updateProduct(PointsProduct product) {
        pointsProductMapper.update(product);
    }

    public void deleteProduct(Integer id) {
        pointsProductMapper.deleteById(id);
    }
}