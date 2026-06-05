package com.back.domain.order.order.dto;

import java.util.List;

public class OrderRequestDto {
    public record CreateOrderRequest(String email,
                                     String address,
                                     List<OrderItemRequestDto> orderItems) {}
}
