package com.flower.service;

import com.flower.entity.Product;
import com.flower.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    public List<Product> getProductList() {
        return productMapper.findAll();
    }

    public Product getProductById(Integer id) {
        return productMapper.findById(id);
    }

    public List<Product> getProductsByCategory(Integer categoryId) {
        return productMapper.findByCategory(categoryId);
    }


    // ========== 管理后台方法 ==========
    public int count() {
        return productMapper.count();
    }

    public List<Product> getAllProducts() {
        return productMapper.findAll();
    }

    public void addProduct(Product product) {
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.insert(product);
    }

    public void updateProduct(Product product) {
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.update(product);
    }

    public void deleteProduct(Integer id) {
        productMapper.deleteById(id);
    }

    public void updateStatus(Integer id, String status) {
        productMapper.updateStatus(id, status);
    }


}