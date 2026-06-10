package com.back.domain.order.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PAYMENT_COMPLETE("결제 완료"),
    PREPARING_PRODUCT("상품 준비 중"),
    IN_TRANSIT("배송 중"),
    DELIVERED("배송 완료");

    private final String nickname ;
}
