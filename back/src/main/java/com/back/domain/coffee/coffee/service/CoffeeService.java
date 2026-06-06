package com.back.domain.coffee.coffee.service;

import com.back.domain.coffee.coffee.dto.CoffeeResponseDto;
import com.back.domain.coffee.coffee.entity.Coffee;
import com.back.domain.coffee.coffee.repository.CoffeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoffeeService {

    private final CoffeeRepository coffeeRepository;

    public List<CoffeeResponseDto> getCoffees() {
        return coffeeRepository.findAll()
                .stream()
                .map(CoffeeResponseDto::from)
                .toList();

    }

    public Optional<Coffee> findLatest() {
        return coffeeRepository.findFirstByOrderByIdDesc();
    }
}