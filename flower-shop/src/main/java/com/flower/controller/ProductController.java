package com.flower.controller;

import com.flower.entity.Product;
import com.flower.service.ProductService;
import com.flower.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public Result<List<Product>> getProductList() {
        return Result.success(productService.getProductList());
    }

    @GetMapping("/{id}")
    public Result<Product> getProductById(@PathVariable Integer id) {
        return Result.success(productService.getProductById(id));
    }

    @GetMapping("/category/{categoryId}")
    public Result<List<Product>> getProductsByCategory(@PathVariable Integer categoryId) {
        return Result.success(productService.getProductsByCategory(categoryId));
    }
}
