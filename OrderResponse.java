package com.portfolio.ecommerce.dto;

import com.portfolio.ecommerce.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long userId,
        String customerName,
        OrderStatus status,
        BigDecimal totalAmount,
        String shippingAddress,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {
}
