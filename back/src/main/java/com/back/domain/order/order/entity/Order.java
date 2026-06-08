package com.back.domain.order.order.entity;

import com.back.domain.order.orderItem.entity.OrderItem;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.CascadeType;
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
@Table(name = "Orders")
@Getter
@NoArgsConstructor
public class Order extends BaseEntity {
    private String email;
    private String address;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> orderItems = new ArrayList<>();

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
    }

    public static Order create (String email, String address) {
        Order order = new Order();
        order.email = email;
        order.address = address;
        return order;
    }
}
