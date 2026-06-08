package com.back.domain.order.order.controller;

import com.back.domain.order.order.entity.Order;
import com.back.domain.order.order.repository.OrderRepository;
import com.back.domain.order.orderItem.entity.OrderItem;
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
    @DisplayName("GET /admin/orders")
    void getAdminOrdersTest() throws Exception {
        Product product = productRepository.save(
                new Product("맛있는 원두", 2000, 10, "image.com")
        );

        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = OrderItem.create(2000, 10);
        orderItem.assignProduct(product);
        orderItems.add(orderItem);

        Order order = Order.create("input@naver.com", "서울 OO구", orderItems);
        orderRepository.save(order);

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
    @DisplayName("POST /orders")
    void createOrderTest() throws Exception {
        Long p1Id = productRepository.save(new Product("맛있는 원두", 2000, 10, "image.com")).getId();
        Long p2Id = productRepository.save(new Product("맛없는 원두", 3000, 20, "image.com")).getId();
        String requestBody = String.format("{" +
                "\"email\" : \"input@naver.com\"," +
                "\"address\": \"서울 OO구 OO로, OO아파트 OO동 OO호\"," +
                "\"orderItems\" : [ "+
                "    {" +
                "    \"productId\": %d," +
                "    \"amount\": 10" +
                "  }," +
                "  {\n" +
                "    \"productId\": %d," +
                "    \"amount\": 15" +
                "  }" +
                "]" +
                "}",p1Id,p2Id);
        mockMvc.perform(
                post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.email").value("input@naver.com"))
                .andExpect(jsonPath("$.data.address").value("서울 OO구 OO로, OO아파트 OO동 OO호"))
                .andExpect(jsonPath("$.data.orderItems[0].name").value("맛있는 원두"))
                .andExpect(jsonPath("$.data.orderItems[0].amount").value(10))
                .andExpect(jsonPath("$.data.orderItems[0].price").value(2000));
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
