package com.back.domain.order.order.controller;

import com.back.domain.order.order.dto.AdminLoginRequestDto;
import com.back.domain.order.order.dto.OrderResponseDto;
import com.back.domain.order.order.service.OrderService;
import com.back.global.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/orders")
    public ResponseEntity<ResponseDto<List<OrderResponseDto>>> adminOrderList() {
        List<OrderResponseDto> orders = orderService.adminOrderList();
        return ResponseEntity.ok(new ResponseDto<>("200", "주문 목록 조회 완료", orders));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<Void>> login(@RequestBody AdminLoginRequestDto requestDto) {
        orderService.login(requestDto);
        return ResponseEntity.ok(new ResponseDto<>("200-1", "로그인 되었습니다.", null));

    }
}