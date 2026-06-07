package com.back.domain.coffee.coffee.controller;

import com.back.domain.coffee.coffee.dto.CoffeeRequestDto;
import com.back.domain.coffee.coffee.dto.CoffeeResponseDto;
import com.back.domain.coffee.coffee.entity.Coffee;
import com.back.domain.coffee.coffee.service.CoffeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/admin/coffees")
@RequiredArgsConstructor
public class AdminCoffeeController {
    private final CoffeeService coffeeService;

    public record TempResponseDto<T>(
            String resultCode,
            String message,
            T data
    ) {
    }

    @PostMapping
    private ResponseEntity<TempResponseDto<CoffeeResponseDto>> create(
            @RequestBody @Valid CoffeeRequestDto.CreateCoffeeRequest requestDto
    ) {
        final Coffee coffee = coffeeService.create(
                requestDto.name(),
                requestDto.price(),
                requestDto.stock(),
                requestDto.imgUrl()
        );
        return new ResponseEntity<>(
                new TempResponseDto<>(
                        "201-1",
                        "상품이 추가되었습니다",
                        CoffeeResponseDto.from(coffee)
                ),
                CREATED
        );
    }
}
