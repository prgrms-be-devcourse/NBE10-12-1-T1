package com.back.domain.order.order.controller;

import com.back.domain.order.entity.Order;
import com.back.domain.order.enums.OrderStatus;
import com.back.domain.order.repository.OrderRepository;
import com.back.domain.order.entity.OrderItem;
import com.back.domain.product.entity.Product;
import com.back.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
                orderItems
        );

        orderRepository.save(order);

        mockMvc.perform(get("/admin/orders/{id}/order-items", order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("주문 아이템 목록 조회 성공"))
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
        //변경해야 테스트 구동 가능 추후 구현 시 다시 되돌려야 할까요?
        OrderItem orderItem = OrderItem.create(pid, product.getName(), product.getPrice(), 10);
        orderItems.add(orderItem);

        Order order = Order.create("input@naver.com", "서울 OO구", OrderStatus.PAYMENT_COMPLETE);
        orderRepository.save(order);
        orderItem.assignOrder(order);

        mockMvc.perform(get("/api/v1/admin/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("주문 목록 조회 성공"))
                .andExpect(jsonPath("$.data[0].id").value(order.getId()))
                .andExpect(jsonPath("$.data[0].email").value("input@naver.com"))
                .andExpect(jsonPath("$.data[0].address").value("서울 OO구"))
                .andExpect(jsonPath("$.data[0].status").value("결제 완료"))
                .andExpect(jsonPath("$.data[0].createdAt").exists());
    }

    @Test
    @DisplayName("POST api/v1/orders")
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
        sb.append("\"amount\":").append("\"").append(20).append("\"");
        sb.append("}");
        sb.append("]");
        sb.append("}");

        mockMvc.perform(
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
                .andExpect(jsonPath("$.data.orderItems[1].amount").value(20))
                .andExpect(jsonPath("$.data.orderItems[1].price").value(3000));
        Product product1 = productRepository.findById(p1Id).get();
        Product product2 = productRepository.findById(p2Id).get();
        Assertions.assertEquals(0,product1.getStock());
        Assertions.assertEquals(5,product2.getStock());
    }

    @Test
    void adminLogin() throws Exception {
        String body = """
                {
                    "id": "admin",
                    "password": "admin"
                }
                """;

        mockMvc.perform(post("/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("로그인 되었습니다."))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
