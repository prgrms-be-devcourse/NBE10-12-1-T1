package com.back.domain.order.entity;

import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;

enum OrderStatus {
    PAYMENT_COMPLETE,
    PREPARING_PRODUCT,
    IN_TRANSIT,
    DELIVERED
}

@Entity
@Table(name = "Orders") // Order는 SQL 예약어라 Table을 생성할 수 없다.
@Getter
@NoArgsConstructor
@AllArgsConstructor
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
    private List<OrderItem> orderItems = new ArrayList<>();

    // 생성자로 구현하는 건 어떨까요?
    public static Order create(String email, String address, List<OrderItem> orderItemsList) {
        Order order = new Order();
        order.email = email;
        order.address = address;
        for (OrderItem orderItem : orderItemsList) {
            orderItem.assignOrder(order);
            order.orderItems.add(orderItem);
        }
        return order;
    }
}
