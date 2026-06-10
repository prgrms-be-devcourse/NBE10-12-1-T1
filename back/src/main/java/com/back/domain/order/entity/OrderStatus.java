package com.back.domain.order.entity;

import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;

@Getter
public enum OrderStatus {
    PAYMENT_COMPLETE("결제 완료"),
    PREPARING_PRODUCT("상품 준비 중"),
    IN_TRANSIT("배송 중"),
    DELIVERED("배송 완료");

    private final String name;

    private OrderStatus(String name) {
        this.name = name;
    }
}
