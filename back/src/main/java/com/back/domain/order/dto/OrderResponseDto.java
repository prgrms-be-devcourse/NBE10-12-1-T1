package com.back.domain.order.dto;


import com.back.domain.order.entity.Order;
import com.back.domain.order.entity.OrderItem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public record OrderResponseDto(Long id,
                               Long deliveryId,
                               String email,
                               String address,
                               List<OrderItemResponseDto> orderItems,
                               Integer totalPrice,
                               String status,
                               LocalDateTime createdAt) {
    public static OrderResponseDto from(Order order) {
        List<OrderItemResponseDto> items = new ArrayList<>();

        for(OrderItem orderItem : order.getOrderItems()) {
            items.add(new OrderItemResponseDto(
                    orderItem.getName(),
                    orderItem.getAmount(),
                    orderItem.getPrice()
            ));
        }

        return new OrderResponseDto(
                order.getId(),
                order.getDeliveryId(),
                order.getEmail(),
                order.getAddress(),
                items,
                order.getTotalPrice(),
                order.getStatus().getNickname(),
                order.getCreatedAt()
        );
    }
}

