package com.back.domain.order.order.controller;

import com.back.domain.coffee.coffee.entity.Coffee;
import com.back.domain.order.order.entity.Order;
import com.back.domain.order.order.repository.OrderRepository;
import com.back.domain.orderItem.orderItem.entity.OrderItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderRepository orderRepository;

    @Test
    void getOrdersTest() throws Exception {
        Order order = mock(Order.class);
        OrderItem firstItem = mock(OrderItem.class);
        OrderItem secondItem = mock(OrderItem.class);
        Coffee firstCoffee = mock(Coffee.class);
        Coffee secondCoffee = mock(Coffee.class);

        when(firstItem.getCoffee()).thenReturn(firstCoffee);
        when(firstCoffee.getName()).thenReturn("맛있는 원두");
        when(firstItem.getAmount()).thenReturn(10);
        when(firstItem.getPrice()).thenReturn(2000);

        when(secondItem.getCoffee()).thenReturn(secondCoffee);
        when(secondCoffee.getName()).thenReturn("더 맛있는 원두");
        when(secondItem.getAmount()).thenReturn(15);
        when(secondItem.getPrice()).thenReturn(3000);

        when(order.getId()).thenReturn(1);
        when(order.getEmail()).thenReturn("input@naver.com");
        when(order.getAddress()).thenReturn("서울 OO구 OO로");
        when(order.getCreatedAt()).thenReturn(
                LocalDateTime.of(2026, 6, 5, 12, 12, 12)
        );
        when(order.getOrderItems())
                .thenReturn(List.of(firstItem, secondItem));

        when(orderRepository.findAll()).thenReturn(List.of(order));

        mockMvc.perform(get("/admin/orders"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message")
                        .value("주문 목록 조회 완료"))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].email")
                        .value("input@naver.com"))
                .andExpect(jsonPath("$.data[0].orderItems.length()")
                        .value(2))
                .andExpect(jsonPath("$.data[0].orderItems[0].name")
                        .value("맛있는 원두"))
                .andExpect(jsonPath("$.data[0].totalPrice")
                        .value(65_000));
    }
}