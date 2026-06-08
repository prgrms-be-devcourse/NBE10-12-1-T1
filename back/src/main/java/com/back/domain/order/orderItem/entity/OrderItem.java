package com.back.domain.order.orderItem.entity;

import com.back.domain.coffee.coffee.entity.Coffee;
import com.back.domain.order.order.entity.Order;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "OrderItem")
@Getter
@NoArgsConstructor
public class OrderItem extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coffeeId")
    private Coffee coffee;

    @Column
    private int amount;

    @Column
    private int price;
}