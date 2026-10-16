package com.flower.controller;

import com.flower.dto.OrderDTO;
import com.flower.entity.Order;
import com.flower.entity.OrderItem;
import com.flower.service.OrderService;
import com.flower.utils.JwtUtil;
import com.flower.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    private Long getUserId(HttpServletRequest request) {
        return JwtUtil.getUserIdFromRequest(request);
    }

    @PostMapping
    public Result<?> createOrder(@RequestBody OrderDTO orderDTO, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error("未登录");
        }
        orderService.createOrder(userId, orderDTO);
        return Result.success("订单创建成功");
    }

    @GetMapping
    public Result<List<Order>> getOrders(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.error("未登录");
        }
        return Result.success(orderService.getOrdersByUserId(userId));
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getOrderDetail(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        List<OrderItem> items = orderService.getOrderItems(id);

        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("items", items);
        return Result.success(result);
    }

    @PutMapping("/{id}/pay")
    public Result<?> payOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 0) {
            return Result.error("订单状态不正确");
        }
        order.setStatus(1);
        orderService.updateOrderStatus(order);
        return Result.success("支付成功");
    }

    @PutMapping("/{id}/cancel")
    public Result<?> cancelOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 0) {
            return Result.error("只能取消待付款订单");
        }
        order.setStatus(4);
        orderService.updateOrderStatus(order);
        return Result.success("取消成功");
    }
}