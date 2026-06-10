package com.back.domain.order.repository;

import com.back.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findTopByEmailAndAddressAndCreatedAtBetween(String email, String address, LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);

    @Query("SELECT MAX(o.deliveryId) FROM Order o")
    Long findMaxDeliveryId();


}
