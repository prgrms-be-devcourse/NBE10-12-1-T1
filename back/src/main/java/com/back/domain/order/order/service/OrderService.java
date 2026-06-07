package com.back.domain.order.order.service;

import com.back.domain.order.order.dto.OrderResponseDto;
import com.back.domain.order.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
//@Transactional 이건 어떠실까용?? CoffeeService에 있는 내용과 동일합니당
public class OrderService {
    private final OrderRepository orderRepository;

    @Transactional
    public List<OrderResponseDto> adminOrderList() {
        return orderRepository.findAll()
                .stream()
                .map(OrderResponseDto::from)
                .toList();
    }
}