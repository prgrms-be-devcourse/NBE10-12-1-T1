package com.back.domain.order.dto;

import java.time.LocalDateTime;

public record OrderResponseDto(Integer orderId, String ordererEmail, LocalDateTime orderedTime) {
}
