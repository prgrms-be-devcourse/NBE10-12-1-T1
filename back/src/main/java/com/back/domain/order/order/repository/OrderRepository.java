package com.back.domain.order.order.repository;

import com.back.domain.delivery.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Delivery, Long> {
}