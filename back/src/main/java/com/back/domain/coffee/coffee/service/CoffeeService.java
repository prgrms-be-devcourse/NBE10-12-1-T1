package com.back.domain.coffee.coffee.service;

import com.back.domain.coffee.coffee.dto.CoffeeResponseDto;
import com.back.domain.coffee.coffee.repository.CoffeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional( readOnly = true)
public class CoffeeService {

    private final CoffeeRepository coffeeRepository;

    public List<CoffeeResponseDto> getCoffees() {
        return coffeeRepository.findAll()
                .stream()
                .map(CoffeeResponseDto::from)
                .toList();

    }

}