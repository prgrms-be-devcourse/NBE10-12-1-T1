package com.back.domain.order.service;

import com.back.domain.order.dto.OrderItemResponseDto;
import com.back.domain.order.dto.OrderRequestDto.CreateOrderRequest;
import com.back.domain.order.dto.OrderResponseDto;
import com.back.domain.order.entity.Order;
import com.back.domain.order.entity.OrderItem;
import com.back.domain.order.repository.OrderRepository;
import com.back.domain.product.entity.Product;
import com.back.domain.product.repository.ProductRepository;
import com.back.global.exception.DuplicateProductException;
import com.back.global.exception.InsufficientStockException;
import com.back.global.exception.OrderNotFoundException;
import com.back.global.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public List<OrderResponseDto> adminOrderList() {
        return orderRepository.findAll()
                .stream()
                .map(OrderResponseDto::from)
                .toList();
    }

    @Transactional
    public OrderResponseDto createOrder(CreateOrderRequest requestDto) {
        validateDuplicateProducts(requestDto);
        //DB 조건부 재고 차감
        decreaseStocks(requestDto);

        //1. 요청들이 들어온 상품Map 생성 {1 : 1번상품},{2 : 2번상품}
        Map<Long, Product> productMap = makeProductMap(requestDto);

        //2. OrderItem Entity List 생성 및 상품 재고 감소
        List<OrderItem> orderItems = makeOrderItems(requestDto, productMap);

        //3. 기존 배송번호 찾기 Or 신규 배송번호 발급
        Long deliveryId = findOrMakeDeliveryId(requestDto);

        //3. 주문 생성
        Order order = Order.create(
                requestDto.email(),
                requestDto.address(),
                orderItems,
                deliveryId);

        orderRepository.save(order);

        return OrderResponseDto.from(order);
    }

    private Map<Long, Product> makeProductMap(CreateOrderRequest requestDto) {
        List<Long> productIds = requestDto.orderItems().stream()
                .map(item -> item.productId()).toList();
        //log.info("상품 아이디 리스트 : %s".formatted(productIds.toString()));

        Map<Long, Product> productMap = productIds.stream().map(id ->
                        productRepository.findByIdAndDeletedAtIsNull(id)
                                .orElseThrow(ProductNotFoundException::new))
                .collect(Collectors.toMap(Product::getId, p -> p));
        return productMap;
    }

    private List<OrderItem> makeOrderItems(CreateOrderRequest requestDto, Map<Long, Product> productMap) {
        List<OrderItem> orderItems = requestDto.orderItems().stream().map(
                        item ->
                        {
                            Product product = productMap.get(item.productId());

                            return OrderItem.create(
                                    product.getId(),
                                    product.getName(),
                                    product.getPrice(),
                                    item.amount()
                            );
                        })
                .toList();
        return orderItems;
    }

    private Long findOrMakeDeliveryId(CreateOrderRequest requestDto) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime();

        LocalDateTime startDate;
        LocalDateTime endDate;

        if (currentTime.isBefore(LocalTime.of(14, 0))) {
            startDate = LocalDateTime.of(today.minusDays(1), LocalTime.of(14, 0, 0));
            endDate = LocalDateTime.of(today, LocalTime.of(13, 59, 59));
        } else {
            startDate = LocalDateTime.of(today, LocalTime.of(14, 0, 0));
            endDate = LocalDateTime.of(today.plusDays(1), LocalTime.of(13, 59, 59));
        }
        Optional<Order> previousOrder = orderRepository.findTopByEmailAndAddressAndCreatedAtBetween(
                requestDto.email(),
                requestDto.address(),
                startDate,
                endDate
        );

        Long deliveryId = previousOrder.map(Order::getDeliveryId) // 이전 주문이 있으면 그 번호 그대로 사용
                .orElseGet(() -> {
                    // 이전 주문이 없으면(신규 배송이면) DB에서 가장 큰 번호를 찾아 +1
                    Long maxDeliveryId = orderRepository.findMaxDeliveryId();
                    return (maxDeliveryId == null) ? 1L : maxDeliveryId + 1L;
                });
        return deliveryId;
    }

    public List<OrderItemResponseDto> getOrderItems(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(OrderNotFoundException::new);

        return order.getOrderItems().stream()
                .map(OrderItemResponseDto::from)
                .toList();
    }

    private void validateDuplicateProducts(CreateOrderRequest requestDto) {
        List<Long> productIds = requestDto.orderItems().stream()
                .map(item -> item.productId()).toList();

        if (productIds.size() != productIds.stream().distinct().count()) {
            throw new DuplicateProductException();
        }
    }

    private void decreaseStocks(CreateOrderRequest requestDto) {
        //orderItems를 productId기준으로 묶고 수량을 합산
        Map<Long, Integer> amounts = requestDto.orderItems().stream()
                .collect(Collectors.groupingBy(
                        item -> item.productId(),
                        Collectors.summingInt(item -> item.amount())
                ));

        amounts.entrySet().stream()
                //모든 트랜잭션이 같은 순서로 상품을 처리하기 위해 productId기준으로 오름차순 정렬
                .sorted(Map.Entry.comparingByKey())
                //entry에는 amount의 각 줄 (특정 상품이 몇개 주문되었는지) 저장
                .forEach(entry -> {
                    Long productId = entry.getKey();
                    int amount = entry.getValue();

                    //decreaseStockIfAvailable를 통해 조건부 update쿼리 실행
                    //java에서 차감하는게 아니라 DB에서 바로 처리
                    int updatedRows = productRepository
                            .decreaseStockIfAvailable(productId, amount);

                    //update 쿼리를 통해 수정된 행의 개수가 0이면 재고차감 실패
                    if (updatedRows == 0) {
                        //해당 상품이 소프트 삭제된 항목인지 확인
                        if (!productRepository.existsByIdAndDeletedAtIsNull(productId)) {
                            //상품이 없거나 삭제된 상품
                            throw new ProductNotFoundException();
                        }
                        //상품은 있으나 재고부족
                        throw new InsufficientStockException();
                    }
                });
    }
}
