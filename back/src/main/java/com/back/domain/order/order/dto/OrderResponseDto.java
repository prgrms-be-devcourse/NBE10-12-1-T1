package com.back.domain.order.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDto {
    public record OrderResponse(
            @NotNull
            Long id,
            @NotNull
            String email,
            @NotNull
            String address,
            @NotEmpty
            List<OrderItemResponseDto> orderItems, // 응답용 상세 DTO 리스트
            @NotNull
            Integer totalPrice,                    // 총 결제 금액
            @NotNull
            LocalDateTime createdAt) {}

    public record OrderWithIsShippedResponse(
            @NotNull
            Long id,
            @NotNull
            String email,
            @NotNull
            String address,
            @NotEmpty
            List<OrderItemResponseDto> orderItems, // 응답용 상세 DTO 리스트
            @NotNull
            Integer totalPrice,                    // 총 결제 금액
            @NotNull
            Boolean isShipped,
            @NotNull
            LocalDateTime createdAt) {}
}


