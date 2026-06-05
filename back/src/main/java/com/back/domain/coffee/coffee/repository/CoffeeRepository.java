package com.back.domain.coffee.coffee.repository;

import com.back.domain.delivery.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoffeeRepository extends JpaRepository<Delivery, Long> {
}