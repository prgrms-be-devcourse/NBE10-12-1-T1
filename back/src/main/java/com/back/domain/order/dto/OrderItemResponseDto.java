package com.back.domain.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemResponseDto(
        @NotNull
        String name,      // 커피 이름 (새로 추가됨)
        @NotNull
        @Min(1)
        Integer amount,   // 수량
        @NotNull
        @Min(1)
        Integer price) {
}
