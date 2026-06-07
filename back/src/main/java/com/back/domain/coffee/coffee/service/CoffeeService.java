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
public class CoffeeService {
    private final CoffeeRepository coffeeRepository;

    @Transactional(readOnly = true)
    public List<CoffeeResponseDto> getCoffees() {
        return coffeeRepository.findAll()
                .stream()
                .map(CoffeeResponseDto::from)
                .toList();

    }


    @Transactional(readOnly = true)
    public Optional<Coffee> findLatest() {
        return coffeeRepository.findFirstByOrderByIdDesc();
    }


    @Transactional
    public Coffee create(
            String name,
            int price,
            int stock,
            String imgUrl
    ) {
        final Coffee coffee = new Coffee(name, price, stock, imgUrl);
        return coffeeRepository.save(coffee);
    }
}