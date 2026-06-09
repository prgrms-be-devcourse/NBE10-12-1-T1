package com.back.domain.order.dto;


import com.back.domain.order.entity.Order;
import com.back.domain.order.entity.OrderItem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public record OrderResponseDto(Long id,
                               String email,
                               String address,
                               List<OrderItemResponseDto> orderItems,
                               Integer totalPrice,
                               LocalDateTime createdAt) {
    public static OrderResponseDto from(Order order) {
        List<OrderItemResponseDto> items = new ArrayList<>();

        int totalPrice = 0;

        for(OrderItem orderItem : order.getOrderItems()) {
            items.add(new OrderItemResponseDto(
                    orderItem.getName(),
                    orderItem.getAmount(),
                    orderItem.getPrice()
            ));
            totalPrice += orderItem.getPrice() * orderItem.getAmount();
        }

        return new OrderResponseDto(
                order.getId(),
                order.getEmail(),
                order.getAddress(),
                items,
                totalPrice,
                order.getCreatedAt()
        );
    }
}

