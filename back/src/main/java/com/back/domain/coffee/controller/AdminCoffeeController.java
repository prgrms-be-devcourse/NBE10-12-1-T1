package com.back.domain.coffee.controller;

import com.back.domain.coffee.dto.CoffeeRequestDto.*;
import com.back.domain.coffee.dto.CoffeeResponseDto;
import com.back.domain.coffee.entity.Coffee;
import com.back.domain.coffee.service.CoffeeService;
import com.back.global.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // 그 호출이 너무 길어져서 줄여봤습니당 ㅎ CoffeeRequestDto.*;
    @PostMapping
    private ResponseEntity<TempResponseDto<CoffeeResponseDto>> create(
            @RequestBody @Valid CreateCoffeeRequest requestDto
    ) {
        //이렇게 쓰는것도 좋은데 예를 들어서 필드 7~8개되면 어떻게 될까용??
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
    @GetMapping
    public ResponseEntity<ResponseDto<List<CoffeeResponseDto>>> getCoffees() {
        List<CoffeeResponseDto> coffees = coffeeService.getCoffees();
        return ResponseEntity.ok(new ResponseDto<>("200", "상품 목록 조회 성공", coffees));
    }
}
