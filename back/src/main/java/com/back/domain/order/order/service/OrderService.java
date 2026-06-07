package com.back.domain.order.order.service;

import com.back.domain.order.order.dto.AdminLoginRequestDto;
import com.back.domain.order.order.dto.OrderResponseDto;
import com.back.domain.order.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    private static final String ADMIN_ID = "admin";
    private static final String ADMIN_PW = "admin";

    @Transactional
    public List<OrderResponseDto> adminOrderList() {
        return orderRepository.findAll()
                .stream()
                .map(OrderResponseDto::from)
                .toList();
    }

    public void login(AdminLoginRequestDto requestDto) {
        if (!ADMIN_ID.equals(requestDto.id()) || !ADMIN_PW.equals(requestDto.password())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
    }
}