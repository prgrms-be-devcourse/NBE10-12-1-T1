package com.back.domain.order.entity;

import com.back.domain.order.enums.OrderStatus;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;

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

    public static Order create(String email, String address, List<OrderItem> orderItems) {
        Order order = new Order();
        order.email = email;
        order.address = address;
        order.status = OrderStatus.PAYMENT_COMPLETE;
        order.addItemsAndSetTotalPrice(orderItems);

        return order;
    }

    private void addItemsAndSetTotalPrice(List<OrderItem> orderItems) {
        int totalPrice = 0;
        for(OrderItem item : orderItems) {
            totalPrice += (item.getPrice() * item.getAmount());

            this.getOrderItems().add(item);
            item.assignOrder(this);
        }
        this.totalPrice = totalPrice;
    }

    public void advanceToNextStatus() {
        if(this.status == OrderStatus.PAYMENT_COMPLETE) this.status = OrderStatus.PREPARING_PRODUCT;
        else if(this.status == OrderStatus.PREPARING_PRODUCT) this.status = OrderStatus.IN_TRANSIT;
        else if(this.status == OrderStatus.IN_TRANSIT) this.status = OrderStatus.DELIVERED;
    }

}