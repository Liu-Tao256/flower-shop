package com.flower.controller;

import com.flower.entity.PointsProduct;
import com.flower.service.PointsProductService;
import com.flower.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/points-products")
public class AdminPointsProductController {

    @Autowired
    private PointsProductService pointsProductService;

    @GetMapping
    public Result<List<PointsProduct>> getAll() {
        return Result.success(pointsProductService.getAllProductsForAdmin());
    }

    @GetMapping("/{id}")
    public Result<PointsProduct> getById(@PathVariable Integer id) {
        return Result.success(pointsProductService.getProductById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody PointsProduct product) {
        pointsProductService.addProduct(product);
        return Result.success("添加成功");
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Integer id, @RequestBody PointsProduct product) {
        product.setId(id);
        pointsProductService.updateProduct(product);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        pointsProductService.deleteProduct(id);
        return Result.success("删除成功");
    }
}