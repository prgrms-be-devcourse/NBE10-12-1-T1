package com.back.domain.order.order.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderItemResponseDto(String name,      // 커피 이름 (새로 추가됨)
                                   Integer amount,   // 수량
                                   Integer price) {
}
