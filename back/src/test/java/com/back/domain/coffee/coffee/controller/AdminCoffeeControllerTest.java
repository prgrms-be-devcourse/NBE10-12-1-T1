package com.back.domain.coffee.coffee.controller;

import com.back.domain.coffee.coffee.dto.CoffeeResponseDto;
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

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    @Test
    @DisplayName("GET /admin/coffees - 커피 목록 조회 성공")
    void getCoffees() throws Exception {
            coffeeService.create("맛있는 원두", 30000, 200, "coffee1.jpg");
            coffeeService.create("더꿀맛 원두", 8923, 400, "coffee2.jpg");

        mockMvc.perform(get("/admin/coffees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("맛있는 원두"))
                .andExpect(jsonPath("$.data[0].price").value(30000));
    }

    @Test
    @DisplayName("GET /admin/coffees - 커피가 없으면 빈 배열 반환")
    void getCoffees_empty() throws Exception {

        mockMvc.perform(get("/admin/coffees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }
}
