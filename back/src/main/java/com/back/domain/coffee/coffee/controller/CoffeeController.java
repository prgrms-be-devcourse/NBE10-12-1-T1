package com.back.domain.coffee.coffee.controller;

import com.back.domain.coffee.coffee.dto.CoffeeResponseDto;
import com.back.domain.coffee.coffee.service.CoffeeService;
import com.back.global.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/coffees")
@RequiredArgsConstructor
public class CoffeeController {

    private final CoffeeService coffeeService;

    @GetMapping
    public ResponseEntity<ResponseDto<List<CoffeeResponseDto>>> getCoffees() {
        List<CoffeeResponseDto> coffees = coffeeService.getCoffees();
        // ResponseDto 내부에 int status --> String resultCode로 변경에 따른 수정
        return ResponseEntity.ok(new ResponseDto<>("200-1", "상품 목록 조회 성공", coffees));
    }
}