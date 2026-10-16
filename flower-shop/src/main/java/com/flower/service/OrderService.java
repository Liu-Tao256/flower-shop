package com.flower.service;

import com.flower.dto.OrderDTO;
import com.flower.entity.Order;
import com.flower.entity.OrderItem;
import com.flower.mapper.OrderMapper;
import com.flower.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.time.LocalDate;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    @Transactional
    public void createOrder(Long userId, OrderDTO orderDTO) {
        String orderNo = generateOrderNo();

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setAddressId(orderDTO.getAddressId());
        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setDeliveryFee(orderDTO.getDeliveryFee());
        order.setFinalAmount(orderDTO.getFinalAmount());
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setRemark(orderDTO.getRemark());
        order.setStatus(0);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        orderMapper.insert(order);

        for (OrderDTO.OrderItemDTO itemDTO : orderDTO.getItems()) {
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(itemDTO.getProductId());
            item.setProductName(itemDTO.getProductName());
            item.setPrice(itemDTO.getPrice());
            item.setQuantity(itemDTO.getQuantity());
            item.setTotalPrice(itemDTO.getTotalPrice());
            orderMapper.insertItem(item);

            // 增加商品销量
            if (itemDTO.getProductId() != null) {
                System.out.println("增加销量: productId=" + itemDTO.getProductId() + ", quantity=" + itemDTO.getQuantity());
                productMapper.increaseSales(itemDTO.getProductId(), itemDTO.getQuantity());
            }
        }
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderMapper.findByUserId(userId);
    }

    public Order getOrderById(Long id) {
        return orderMapper.findById(id);
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        return orderMapper.findItemsByOrderId(orderId);
    }

    public void updateOrderStatus(Order order) {
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.updateStatus(order);
    }

    // ========== 管理后台方法 ==========
    public int count() {
        return orderMapper.count();
    }

    public BigDecimal getTotalSales() {
        BigDecimal total = orderMapper.getTotalSales();
        return total == null ? BigDecimal.ZERO : total;
    }

    public List<Order> getRecentOrders(int limit) {
        return orderMapper.findRecent(limit);
    }

    public List<Order> getAllOrders(String keyword, Integer status) {
        return orderMapper.findAll(keyword, status);
    }

    public List<Order> getAllOrders() {
        return orderMapper.findAll(null, null);
    }

    public void updateOrderStatus(Long id, Integer status) {
        orderMapper.updateStatusById(id, status);
    }

    // ========== 订单趋势方法 ==========
    public List<String> getLast7Days() {
        List<String> days = new java.util.ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            days.add(date.format(formatter));
        }
        return days;
    }

    public List<Integer> getLast7DaysOrderCounts() {
        List<Integer> counts = new java.util.ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            int count = orderMapper.countByDate(date.toString());
            counts.add(count);
        }
        return counts;
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = new Random().nextInt(10000);
        return timestamp + String.format("%04d", random);
    }
}