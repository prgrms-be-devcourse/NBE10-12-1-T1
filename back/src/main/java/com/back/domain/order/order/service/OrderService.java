package com.back.domain.order.order.service;

import com.back.domain.order.order.dto.AdminLoginRequestDto;
import com.back.domain.order.order.dto.OrderRequestDto.*;
import com.back.domain.order.order.dto.OrderResponseDto;
import com.back.domain.order.order.entity.Order;
import com.back.domain.order.order.repository.OrderRepository;
import com.back.domain.order.orderItem.entity.OrderItem;
import com.back.domain.product.entity.Product;
import com.back.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
//@Transactional 이건 어떠실까용?? ProductService에 있는 내용과 동일합니당
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    private static final String ADMIN_ID = "admin";
    private static final String ADMIN_PW = "admin";

    @Transactional
    public List<OrderResponseDto> adminOrderList() {
        return orderRepository.findAll()
                .stream()
                .map(OrderResponseDto::from)
                .toList();
    }

    @Transactional
    public OrderResponseDto createOrder(CreateOrderRequest requestDto) {
        List<Long> productIds = requestDto.orderItems().stream()
                .map(item -> item.productId()).toList();
        Map<Long, Product> productMap = productIds.stream().map(id ->
                        productRepository.findById(id)
                                .orElseThrow(NoSuchElementException::new))
                .collect(Collectors.toMap(Product::getId, p -> p));

        List<OrderItem> orderItems = requestDto.orderItems().stream().map(
                item ->
                {
                    Product product = productMap.get(item.productId());
                    product.decreaseStock(item.amount());
                    OrderItem orderItem = OrderItem.create(
                            product.getPrice(),
                            item.amount()
                    );
                    orderItem.assignProduct(productMap.get(item.productId()));
                    return orderItem;
                })
                .toList();

        Order order = Order.create(requestDto.email(), requestDto.address(), orderItems);
        orderRepository.save(order);
        return OrderResponseDto.from(order);
    }

    public void login(AdminLoginRequestDto requestDto) {
        if (!ADMIN_ID.equals(requestDto.id()) || !ADMIN_PW.equals(requestDto.password())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
    }
}
