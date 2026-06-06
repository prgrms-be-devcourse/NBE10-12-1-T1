package com.back.domain.delivery.delivery.entity;

import com.back.domain.order.order.entity.Order;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class Delivery extends BaseEntity {
    private boolean isShipped;
    @OneToOne
    private Order order;
}