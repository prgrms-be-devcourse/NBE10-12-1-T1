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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private Product product;

    private int price;
    private int amount;

    @Column
    private int price;

    public static OrderItem create(Order order, Product product, int amount, int price) {
        OrderItem orderItem = new OrderItem();
        orderItem.order = order;
        orderItem.product = product;
        return orderItem;
    }

    public static OrderItem create(int price, int amount) {
        OrderItem orderItem = new OrderItem();
        orderItem.amount = amount;
        orderItem.price = price;
        return orderItem;
    }

    public void assignOrder(Order order) {
        this.order = order;
    }

    public void assignProduct(Product product) {
        this.product = product;
    }

}
