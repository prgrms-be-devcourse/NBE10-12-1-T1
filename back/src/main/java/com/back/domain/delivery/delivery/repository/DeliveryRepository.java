package com.back.domain.delivery.delivery.repository;

import com.back.domain.delivery.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}