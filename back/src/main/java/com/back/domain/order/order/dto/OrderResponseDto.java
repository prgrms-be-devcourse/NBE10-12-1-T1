package com.back.domain.order.order.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDto(Long id,
                               String email,
                               String address,
                               List<OrderItemResponseDto> orderItems, // 응답용 상세 DTO 리스트
                               Integer totalPrice,                    // 총 결제 금액
                               LocalDateTime createdAt) {
}
