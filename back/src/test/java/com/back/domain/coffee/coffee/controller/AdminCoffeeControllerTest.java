package com.back.domain.coffee.coffee.controller;

import com.back.domain.coffee.coffee.entity.Coffee;
import com.back.domain.coffee.coffee.service.CoffeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminCoffeeControllerTest {
    private final MockMvc mockMvc;
    private final CoffeeService coffeeService;

    @Autowired
    public AdminCoffeeControllerTest(MockMvc mockMvc, CoffeeService coffeeService) {
        this.mockMvc = mockMvc;
        this.coffeeService = coffeeService;
    }

    @Test
    @DisplayName("POST /admin/coffees")
    void t1() throws Exception {
        final ResultActions resultActions = mockMvc
                .perform(
                        post("/admin/coffees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "name" : "NEW원두",
                                            "price" : 12000,
                                            "stock" : 300,
                                            "imgUrl" : "coffee1.jpg"
                                        }
                                        """)
                ).andDo(print());

        final Coffee coffee = coffeeService.findLatest().orElseThrow();

        resultActions
                .andExpect(handler().handlerType(AdminCoffeeController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.message").value("상품이 추가되었습니다"))
                .andExpect(jsonPath("$.data.id").value(coffee.getId()))
                .andExpect(jsonPath("$.data.name").value("NEW원두"))
                .andExpect(jsonPath("$.data.price").value(12000))
                .andExpect(jsonPath("$.data.stock").value(300))
                .andExpect(jsonPath("$.data.imgUrl").value("coffee1.jpg"))
        ;
    }
}
