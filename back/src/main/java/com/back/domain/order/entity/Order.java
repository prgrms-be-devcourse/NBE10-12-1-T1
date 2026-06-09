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
enum OrderStatus {
    PAYMENT_COMPLETE("결제 완료"),
    PREPARING_PRODUCT("상품 준비 중"),
    IN_TRANSIT("배송 중"),
    DELIVERED("배송 완료");

    private final String name;

    private OrderStatus(String name) {
        this.name = name;
    }
}

@Entity
@Table(name = "Orders") // Order는 SQL 예약어라 Table을 생성할 수 없다.
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class Order extends BaseEntity {
    private String email;
    private String address;
    @Enumerated(STRING)
    private OrderStatus status;
    private int totalPrice;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<OrderItem> orderItems = new ArrayList<>();

    public static Order create(String email, String address, OrderStatus status) {
        int totalPrice = 0; // 구현되어야 합니다.
        final Order order = new Order(email, address, status, totalPrice);
        return order;
    }
}
