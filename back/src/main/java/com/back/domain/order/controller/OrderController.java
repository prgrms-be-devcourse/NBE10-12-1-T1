package com.back.domain.order.controller;

import com.back.domain.order.dto.AdminLoginRequestDto;
import com.back.domain.order.dto.AdminOrderResponseDto;
import com.back.domain.order.dto.OrderRequestDto.*;
import com.back.domain.order.dto.OrderResponseDto;
import com.back.domain.order.service.OrderService;
import com.back.global.annotation.ApiV1;
import com.back.global.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiV1
@RestController
@RequestMapping("")
@RequiredArgsConstructor
@Tag(name = "관리자 시스템 및 주문 관리", description = "관리자 권한 로그인 및 전체 주문 목록 조회")

public class OrderController {
    private final OrderService orderService;

    @GetMapping("/admin/orders")
    @Operation(summary = "주문 목록 조회")
    public ResponseEntity<ResponseDto<List<AdminOrderResponseDto>>> adminOrderList() {
        List<AdminOrderResponseDto> orders = orderService.adminOrderList();
        return ResponseEntity.ok(new ResponseDto<>("200-1", "주문 목록 조회 성공", orders));
    }

    @PostMapping("/orders")
    @Operation(summary = "주문 생성")
    public ResponseDto<OrderResponseDto> createOrder(@RequestBody CreateOrderRequest requestDto) {
        OrderResponseDto response = orderService.createOrder(requestDto);
        return new ResponseDto<>("201-1", "주문 생성 되었습니다.", response);
    }

    @PostMapping("/admin/login")
    @Operation(summary = "관리자 로그인")
    public ResponseDto<Void> login(@RequestBody AdminLoginRequestDto requestDto) {
        orderService.login(requestDto);
        return new ResponseDto<>("200-1", "로그인 되었습니다.", null);

    }
}
