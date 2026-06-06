package com.back.domain.orderItem.orderItem.entity;

import com.back.domain.coffee.coffee.entity.Coffee;
import com.back.domain.order.order.entity.Order;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class OrderItem extends BaseEntity {
    private int amount;
    @ManyToOne
    private Order order;
    @ManyToOne
    private Coffee coffee;
}