package com.back.domain.order.order.dto;

import java.time.LocalDateTime;

public record OrderResponseDto(Long orderId, String ordererEmail, LocalDateTime orderedTime) {
}
