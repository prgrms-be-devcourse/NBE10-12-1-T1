package com.back.domain.order.order.dto;

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
}
