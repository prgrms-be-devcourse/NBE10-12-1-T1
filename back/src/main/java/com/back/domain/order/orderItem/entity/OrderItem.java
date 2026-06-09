package com.back.domain.order.orderItem.entity;

import com.back.domain.order.order.entity.Order;
import com.back.domain.product.entity.Product;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class OrderItem extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId")
    private Order order;
    private int productId;
    private String name;
    private int price;
    private int amount;

    public static OrderItem create(String name, int price, int amount) {
        OrderItem orderItem = new OrderItem();
        orderItem.name = name;
        orderItem.price = price;
        orderItem.amount = amount;
        return orderItem;
    }

    public void assignOrder(Order order) {
        this.order = order;
    }

}
