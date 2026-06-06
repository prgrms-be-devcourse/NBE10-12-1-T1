package com.back.domain.order.order.dto;

import com.back.domain.order.order.entity.Order;
import com.back.domain.orderItem.orderItem.entity.OrderItem;

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
                    orderItem.getCoffee().getName(),
                    orderItem.getAmount(),
                    orderItem.getPrice()
            ));
            totalPrice += orderItem.getPrice() * orderItem.getAmount();
        }

        return new OrderResponseDto(
                (long) order.getId(),
                order.getEmail(),
                order.getAddress(),
                items,
                totalPrice,
                order.getCreatedAt()
        );
    }
}
