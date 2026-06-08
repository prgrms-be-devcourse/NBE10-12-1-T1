package com.back.domain.order.order.dto;


import com.back.domain.order.order.entity.Order;
import com.back.domain.order.orderItem.entity.OrderItem;

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

        //해당 값은 stream 내부에서는 외부값 수정하면 안되는 규약이 있어서 만드신거죠??
        int totalPrice = 0;

        for(OrderItem orderItem : order.getOrderItems()) {
            items.add(new OrderItemResponseDto(
                    orderItem.getProduct().getName(),
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

