package com.back.domain.order.order.entity;

import com.back.domain.orderItem.orderItem.entity.OrderItem;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Order")
@Getter
@NoArgsConstructor
public class Order extends BaseEntity {
    private String email;
    private String address;

    @Column(nullable = false)
    private boolean shipped;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();
}