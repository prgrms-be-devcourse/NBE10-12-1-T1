package com.back.domain.coffee.coffee.repository;

import com.back.domain.coffee.coffee.entity.Coffee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
}