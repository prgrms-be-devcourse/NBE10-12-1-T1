package com.back.domain.order.order.controller;

import com.back.domain.order.order.entity.Order;
import com.back.domain.order.order.repository.OrderRepository;
import com.back.domain.order.orderItem.entity.OrderItem;
import com.back.domain.product.entity.Product;
import com.back.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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
    @DisplayName("GET /admin/orders")
    void getAdminOrdersTest() throws Exception {
        Product product = productRepository.save(
                new Product("맛있는 원두", 2000, 10, "image.com")
        );

        Order order = Order.create("input@naver.com", "서울 OO구");
        OrderItem orderItem = OrderItem.create(order, product, 10, 2000);

        orderRepository.save(order);
        order.addOrderItem(orderItem);

        mockMvc.perform(get("/admin/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(order.getId()))
                .andExpect(jsonPath("$.data[0].email").value("input@naver.com"))
                .andExpect(jsonPath("$.data[0].address").value("서울 OO구"))
                .andExpect(jsonPath("$.data[0].orderItems[0].name").value("맛있는 원두"))
                .andExpect(jsonPath("$.data[0].orderItems[0].amount").value(10))
                .andExpect(jsonPath("$.data[0].orderItems[0].price").value(2000));
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
