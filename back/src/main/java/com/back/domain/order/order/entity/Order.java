package com.back.domain.order.order.entity;

import com.back.domain.orderItem.orderItem.entity.OrderItem;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

// entity 이름을 변경해야 할 듯 합니다
// order는 SQL 예약어라 에러 발생하여 Table 생성이 안된다고 합니다

@Entity
@Table(name = "Order")
@Getter
@NoArgsConstructor
public class Order extends BaseEntity {
    private String email;
    private String address;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();
}