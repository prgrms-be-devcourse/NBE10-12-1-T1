package com.back.domain.order.order.controller;

import com.back.domain.order.order.dto.OrderResponseDto;
import com.back.domain.order.order.service.OrderService;
import com.back.global.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ResponseDto<List<OrderResponseDto>>> adminOrderList() {
        List<OrderResponseDto> orders = orderService.adminOrderList();

        // ResponseDto 내부에 int status --> String resultCode로 변경에 따른 수정
        return ResponseEntity.ok(
                new ResponseDto<>(
                        "200-1",
                        "주문 목록 조회 완료",
                        orders
                )
        );
    }
}