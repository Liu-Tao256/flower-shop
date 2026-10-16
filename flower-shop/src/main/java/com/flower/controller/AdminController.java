package com.flower.controller;

import com.flower.entity.Product;
import com.flower.entity.Plant;
import com.flower.entity.Order;
import com.flower.entity.OrderItem;
import com.flower.service.ProductService;
import com.flower.service.PlantService;
import com.flower.service.OrderService;
import com.flower.service.UserService;
import com.flower.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private PlantService plantService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    // ========== 数据看板 ==========
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboard() {
        Map<String, Object> data = new HashMap<>();

        int totalProducts = productService.count();
        int totalOrders = orderService.count();
        int totalUsers = userService.count();
        BigDecimal totalSales = orderService.getTotalSales();
        List<Order> recentOrders = orderService.getRecentOrders(5);
        List<Plant> topPlants = plantService.getTopPlants(5);

        data.put("totalProducts", totalProducts);
        data.put("totalOrders", totalOrders);
        data.put("totalUsers", totalUsers);
        data.put("totalSales", totalSales == null ? 0 : totalSales);
        data.put("recentOrders", recentOrders);
        data.put("topPlants", topPlants);

        return Result.success(data);
    }

    // ========== 订单趋势 ==========
    @GetMapping("/order-trend")
    public Result<Map<String, Object>> getOrderTrend() {
        Map<String, Object> data = new HashMap<>();
        data.put("days", orderService.getLast7Days());
        data.put("counts", orderService.getLast7DaysOrderCounts());
        return Result.success(data);
    }

    // ========== 商品管理 ==========
    @GetMapping("/products")
    public Result<List<Product>> getAllProducts() {
        return Result.success(productService.getAllProducts());
    }

    @GetMapping("/products/{id}")
    public Result<Product> getProductById(@PathVariable Integer id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        return Result.success(product);
    }

    @PostMapping("/products")
    public Result<?> addProduct(@RequestBody Product product) {
        try {
            productService.addProduct(product);
            return Result.success("添加成功");
        } catch (Exception e) {
            return Result.error("添加失败：" + e.getMessage());
        }
    }

    @PutMapping("/products/{id}")
    public Result<?> updateProduct(@PathVariable Integer id, @RequestBody Product product) {
        try {
            product.setId(id);
            productService.updateProduct(product);
            return Result.success("修改成功");
        } catch (Exception e) {
            return Result.error("修改失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/products/{id}")
    public Result<?> deleteProduct(@PathVariable Integer id) {
        try {
            productService.deleteProduct(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    @PutMapping("/products/{id}/status")
    public Result<?> updateProductStatus(@PathVariable Integer id, @RequestParam String status) {
        try {
            productService.updateStatus(id, status);
            return Result.success("状态更新成功");
        } catch (Exception e) {
            return Result.error("状态更新失败：" + e.getMessage());
        }
    }

    // ========== 植物管理 ==========
    @GetMapping("/plants")
    public Result<List<Plant>> getAllPlants(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return Result.success(plantService.searchPlants(keyword));
        }
        return Result.success(plantService.getAllPlants());
    }

    @GetMapping("/plants/{id}")
    public Result<Plant> getPlantById(@PathVariable Integer id) {
        Plant plant = plantService.getPlantDetail(id);
        if (plant == null) {
            return Result.error("植物不存在");
        }
        return Result.success(plant);
    }

    @PostMapping("/plants")
    public Result<?> addPlant(@RequestBody Plant plant) {
        try {
            plantService.addPlant(plant);
            return Result.success("添加成功");
        } catch (Exception e) {
            return Result.error("添加失败：" + e.getMessage());
        }
    }

    @PutMapping("/plants/{id}")
    public Result<?> updatePlant(@PathVariable Integer id, @RequestBody Plant plant) {
        try {
            plant.setId(id);
            plantService.updatePlant(plant);
            return Result.success("修改成功");
        } catch (Exception e) {
            return Result.error("修改失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/plants/{id}")
    public Result<?> deletePlant(@PathVariable Integer id) {
        try {
            plantService.deletePlant(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    @PutMapping("/plants/{id}/relations")
    public Result<?> updatePlantRelations(@PathVariable Integer id, @RequestBody Map<String, List<Integer>> params) {
        List<Integer> productIds = params.get("productIds");
        plantService.updatePlantRelations(id, productIds);
        return Result.success("更新成功");
    }

    // ========== 订单管理 ==========
    @GetMapping("/orders")
    public Result<List<Order>> getAllOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(orderService.getAllOrders(keyword, status));
    }

    @GetMapping("/orders/{id}")
    public Result<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        // 获取订单项
        List<OrderItem> items = orderService.getOrderItems(id);
        order.setItems(items);
        return Result.success(order);
    }

    @PutMapping("/orders/{id}/status")
    public Result<?> updateOrderStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            orderService.updateOrderStatus(id, status);
            return Result.success("状态更新成功");
        } catch (Exception e) {
            return Result.error("状态更新失败：" + e.getMessage());
        }
    }
}