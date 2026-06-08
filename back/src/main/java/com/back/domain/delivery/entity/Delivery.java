package com.back.domain.delivery.entity;

import com.back.domain.order.order.entity.Order;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Delivery extends BaseEntity {
    private boolean isShipped;
    @OneToOne
    private Order order;
}
