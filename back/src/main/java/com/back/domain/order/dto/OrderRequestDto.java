package com.back.domain.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class OrderRequestDto {
    public record CreateOrderRequest(
            @NotBlank(message = "이메일은 필수입니다")
            String email,
            @NotBlank(message = "주소는 필수입니다")
            String address, 
            @Size(min = 1, message = "주문상품은 최소 하나는 있어야합니다")
            @NotEmpty(message = "주문상품은 최소 한개는 있어야합니다")
            @Valid
            List<OrderItemRequestDto> orderItems
    ) {}
}
