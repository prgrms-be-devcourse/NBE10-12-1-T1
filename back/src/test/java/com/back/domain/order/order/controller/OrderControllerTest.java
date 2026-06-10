package com.back.domain.order.order.controller;

import com.back.domain.order.entity.Order;
import com.back.domain.order.entity.OrderItem;
import com.back.domain.order.repository.OrderRepository;
import com.back.domain.product.entity.Product;
import com.back.domain.product.repository.ProductRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderControllerTest {
    private final MockMvc mockMvc;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderControllerTest(MockMvc mockMvc, OrderRepository orderRepository, ProductRepository productRepository) {
        this.mockMvc = mockMvc;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Test
    @DisplayName("관리자 주문 아이템 목록 조회 성공")
    void t1() throws Exception {

        List<OrderItem> orderItems = new ArrayList<>();

        OrderItem orderItem = OrderItem.create(
                1L,
                "맛있는 원두",
                2000,
                10
        );

        orderItems.add(orderItem);

        Order order = Order.create(
                "input@naver.com",
                "서울 OO구",
                orderItems,
                1L
        );

        orderRepository.save(order);

        mockMvc.perform(get("/api/v1/admin/orders/{id}/order-items", order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("관리자 주문 아이템 목록 조회 성공"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].name").value("맛있는 원두"))
                .andExpect(jsonPath("$.data[0].amount").value(10))
                .andExpect(jsonPath("$.data[0].price").value(2000));
    }

    @Test
    @DisplayName("관리자 주문 목록 조회 성공")
    void getAdminOrdersTest() throws Exception {
        Product product = productRepository.save(
                new Product("맛있는 원두", 2000, 10, "image.com")
        );
        Long pid = product.getId();

        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = OrderItem.create(pid, product.getName(), product.getPrice(), 10);
        orderItems.add(orderItem);

        Order order = Order.create("input@naver.com", "서울 OO구", orderItems, 1L);
        orderRepository.save(order);

        mockMvc.perform(get("/api/v1/admin/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("관리자 주문 목록 조회 성공"))
                .andExpect(jsonPath("$.data[0].id").value(order.getId()))
                .andExpect(jsonPath("$.data[0].email").value("input@naver.com"))
                .andExpect(jsonPath("$.data[0].address").value("서울 OO구"))
                .andExpect(jsonPath("$.data[0].status").value("결제 완료"))
                .andExpect(jsonPath("$.data[0].createdAt").exists());
    }

    @Test
    @DisplayName("주문 생성 성공")
    void createOrderTest() throws Exception {
        Long p1Id = productRepository.save(new Product("맛있는 원두", 2000, 10, "image.com")).getId();
        Long p2Id = productRepository.save(new Product("맛없는 원두", 3000, 20, "image.com")).getId();
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"email\":").append("\"").append("input@naver.com").append("\",");
        sb.append("\"address\":").append("\"").append("서울 OO구 OO로, OO아파트 OO동 OO호").append("\",");
        sb.append("\"orderItems\":").append("[");
        sb.append("{");
        sb.append("\"productId\":").append("\"").append(p1Id).append("\",");
        sb.append("\"amount\":").append("\"").append(10).append("\"");
        sb.append("},");
        sb.append("{");
        sb.append("\"productId\":").append("\"").append(p2Id).append("\",");
        sb.append("\"amount\":").append("\"").append(15).append("\"");
        sb.append("}");
        sb.append("]");
        sb.append("}");

        MvcResult result1 = mockMvc.perform(
                post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sb.toString())
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.email").value("input@naver.com"))
                .andExpect(jsonPath("$.data.address").value("서울 OO구 OO로, OO아파트 OO동 OO호"))
                .andExpect(jsonPath("$.data.totalPrice").value(65000))
                .andExpect(jsonPath("$.data.orderItems[0].name").value("맛있는 원두"))
                .andExpect(jsonPath("$.data.orderItems[0].amount").value(10))
                .andExpect(jsonPath("$.data.orderItems[0].price").value(2000))
                .andExpect(jsonPath("$.data.orderItems[1].name").value("맛없는 원두"))
                .andExpect(jsonPath("$.data.orderItems[1].amount").value(15))
                .andExpect(jsonPath("$.data.orderItems[1].price").value(3000))
                .andReturn();
        Product product1 = productRepository.findById(p1Id).get();
        Product product2 = productRepository.findById(p2Id).get();
        Assertions.assertEquals(0,product1.getStock());
        Assertions.assertEquals(5,product2.getStock());
        String responseBody = result1.getResponse().getContentAsString(StandardCharsets.UTF_8);
        Long deliveryId1 = JsonPath.parse(responseBody).read("$.data.deliveryId", Long.class);

        Long p3Id = productRepository.save(new Product("맛있는 원두_새로운맛", 2000, 10, "image.com")).getId();
        Long p4Id = productRepository.save(new Product("맛없는 원두_새로운맛", 3000, 20, "image.com")).getId();
        StringBuilder sb1 = new StringBuilder();
        sb1.append("{");
        sb1.append("\"email\":").append("\"").append("input@naver.com").append("\",");
        sb1.append("\"address\":").append("\"").append("서울 OO구 OO로, OO아파트 OO동 OO호").append("\",");
        sb1.append("\"orderItems\":").append("[");
        sb1.append("{");
        sb1.append("\"productId\":").append("\"").append(p3Id).append("\",");
        sb1.append("\"amount\":").append("\"").append(10).append("\"");
        sb1.append("},");
        sb1.append("{");
        sb1.append("\"productId\":").append("\"").append(p4Id).append("\",");
        sb1.append("\"amount\":").append("\"").append(15).append("\"");
        sb1.append("}");
        sb1.append("]");
        sb1.append("}");

        MvcResult result2 = mockMvc.perform(
                        post("/api/v1/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(sb1.toString())
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.email").value("input@naver.com"))
                .andExpect(jsonPath("$.data.address").value("서울 OO구 OO로, OO아파트 OO동 OO호"))
                .andExpect(jsonPath("$.data.totalPrice").value(65000))
                .andExpect(jsonPath("$.data.orderItems[0].name").value("맛있는 원두_새로운맛"))
                .andExpect(jsonPath("$.data.orderItems[0].amount").value(10))
                .andExpect(jsonPath("$.data.orderItems[0].price").value(2000))
                .andExpect(jsonPath("$.data.orderItems[1].name").value("맛없는 원두_새로운맛"))
                .andExpect(jsonPath("$.data.orderItems[1].amount").value(15))
                .andExpect(jsonPath("$.data.orderItems[1].price").value(3000))
                .andReturn();

        String responseBody2 = result2.getResponse().getContentAsString(StandardCharsets.UTF_8);
        Long deliveryId2 = JsonPath.parse(responseBody2).read("$.data.deliveryId", Long.class);
        Product product3 = productRepository.findById(p3Id).get();
        Product product4 = productRepository.findById(p4Id).get();
        Assertions.assertEquals(0,product3.getStock());
        Assertions.assertEquals(5,product4.getStock());


        Assertions.assertEquals(deliveryId1, deliveryId2);
    }

    @Test
    @DisplayName("주문 생성 성공 조건부 재고 차감")
    //동시성 테스트 이므로 트랜잭션 없이 실행
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void t4() throws Exception {
        Product product = productRepository.save(new Product("맛있는 원두", 2000, 10, "image.com"));

        String email = "input@naver.com";
        Long orderCountBefore = orderRepository.count();

        String requestBody = """
            {
              "email": "%s",
              "address": "서울 OO구",
              "orderItems": [
                {
                  "productId": %d,
                  "amount": 7
                }
              ]
            }
            """.formatted(email, product.getId());

        //동시에 요청 2개를 보내기 위해 스레드 2개 생성
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        Callable<MvcResult> request = () -> {
            //스레드 준비
            ready.countDown();
            //start.countDown 오기 전까지 대기
            start.await();

            return mockMvc.perform(
                    post("/api/v1/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
            ).andReturn();
        };

        try {
            //각각 다른 스레드에서 작업을 시행하므로 Future로 작업 결과를 나중에 받을 수 있게함
            Future<MvcResult> first = executor.submit(request);
            Future<MvcResult> second = executor.submit(request);

            //두 스레드가 준비될때까지 대기
            Assertions.assertTrue(ready.await(5, TimeUnit.SECONDS));
            //동시에 스레드 실행
            start.countDown();

            MvcResult firstResult = first.get();
            MvcResult secondResult = second.get();

            List<Integer> statuses = new ArrayList<>(List.of(
                    firstResult.getResponse().getStatus(),
                    secondResult.getResponse().getStatus()
            ));
            statuses.sort(Integer::compareTo);

            //하나는 성공하고 다른 하나는 재고 부족
            Assertions.assertEquals(List.of(201, 409), statuses);

            Product result = productRepository.findByIdAndDeletedAtIsNull(product.getId()).orElseThrow();

            //성공한 주문 7개만 차감
            Assertions.assertEquals(3, result.getStock());
            //성공한 주문 개수 1개만 생성
            Assertions.assertEquals(orderCountBefore + 1, orderRepository.count());
        } finally {
            //스레드 종료
            executor.shutdownNow();

            //트랜잭션을 미사용하였기 때문에 직접 데이터 정리
            List<Long> orderIds = orderRepository.findAll().stream()
                    .filter(order -> email.equals(order.getEmail()))
                    .map(Order::getId)
                    .toList();

            orderRepository.deleteAllById(orderIds);
            productRepository.deleteById(product.getId());
        }
    }
}
