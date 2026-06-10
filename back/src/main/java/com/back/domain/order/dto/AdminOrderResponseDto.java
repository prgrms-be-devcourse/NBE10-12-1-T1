package com.back.domain.order.dto;

import com.back.domain.order.entity.Order;

import java.time.LocalDateTime;

public record AdminOrderResponseDto (
    Long id,
    String email,
    String address,
    Integer totalPrice,
    String status,
    LocalDateTime createdAt
) {
    public static AdminOrderResponseDto from(Order order) {
        return new AdminOrderResponseDto(
                order.getId(),
                order.getEmail(),
                order.getAddress(),
                order.getTotalPrice(),
                order.getStatus().getNickname(),
                order.getCreatedAt()

        );

    }
}

