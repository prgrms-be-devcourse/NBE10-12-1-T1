package com.back.domain.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequestDto(
        @NotNull(message = "상품 아이디는 필수입니다")
        Long productId,
        @NotNull(message = "상품 개수는 필수입니다")
        @Min(value= 1, message = "상품 개수는 1개이상 이어야 합니다")
        Integer amount
        ){}
