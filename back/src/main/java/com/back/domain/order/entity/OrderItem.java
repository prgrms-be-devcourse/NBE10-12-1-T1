package com.back.domain.order.entity;

import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class OrderItem extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId")
    private Order order;
    private Long productId;
    private String name;
    private int price;
    private int amount;

    public static OrderItem create(Long productId,String name, int price, int amount) {
        OrderItem orderItem = new OrderItem();
        orderItem.productId = productId;
        orderItem.name = name;
        orderItem.price = price;
        orderItem.amount = amount;
        return orderItem;
    }

    public void assignOrder(Order order) {
        this.order = order;
    }

}
