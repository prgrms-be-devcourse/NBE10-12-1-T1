package com.back.domain.order.dto;

import com.back.domain.order.entity.OrderItem;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemResponseDto(
        @NotNull
        String name,
        @NotNull
        @Min(1)
        Integer amount,
        @NotNull
        @Min(1)
        Integer price) {

        public static OrderItemResponseDto from(OrderItem orderItem) {
                return new OrderItemResponseDto(
                        orderItem.getName(),
                        orderItem.getAmount(),
                        orderItem.getPrice()
                );
        }
}
