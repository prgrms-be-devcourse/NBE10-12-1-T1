package com.back.domain.order.service;

import com.back.domain.order.dto.OrderRequestDto.CreateOrderRequest;
import com.back.domain.order.dto.OrderItemResponseDto;
import com.back.domain.order.dto.OrderResponseDto;
import com.back.domain.order.entity.Order;
import com.back.domain.order.entity.OrderItem;
import com.back.domain.order.enums.OrderStatus;
import com.back.domain.order.repository.OrderRepository;
import com.back.domain.product.entity.Product;
import com.back.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public List<OrderResponseDto> adminOrderList() {
        return orderRepository.findAll()
                .stream()
                .map(OrderResponseDto::from)
                .toList();
    }

    public OrderResponseDto createOrder(CreateOrderRequest requestDto) {
        List<Long> productIds = requestDto.orderItems().stream()
                .map(item -> item.productId()).toList();
        log.info("상품 아이디 리스트 : %s".formatted(productIds.toString()));
        Map<Long, Product> productMap = productIds.stream().map(id ->
                        productRepository.findById(id)
                                .orElseThrow(NoSuchElementException::new))
                .collect(Collectors.toMap(Product::getId, p -> p));

        Order order = Order.create(
                requestDto.email(),
                requestDto.address(),
                OrderStatus.PAYMENT_COMPLETE);

        requestDto.orderItems().forEach(item -> {
            Product product = productMap.get(item.productId());
            product.decreaseStock(item.amount());
            OrderItem orderItem = OrderItem.create(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    item.amount()
            );
            orderItem.assignOrder(order);
            order.getOrderItems().add(orderItem);
        });

        orderRepository.save(order);
        return OrderResponseDto.from(order);
    }

    public List<OrderItemResponseDto> getOrderItems(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        return order.getOrderItems().stream()
                .map(OrderItemResponseDto::from)
                .toList();
    }

}
