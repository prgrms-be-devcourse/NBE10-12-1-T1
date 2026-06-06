package com.back.domain.coffee.coffee.controller;


import com.back.domain.coffee.coffee.dto.CoffeeResponseDto;
import com.back.domain.coffee.coffee.service.CoffeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CoffeeController.class)
public class CoffeeControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    CoffeeService coffeeService;

    @Test
    @DisplayName("GET / coffees - 커피 목록 조회 성공")
    void getCoffees() throws Exception {
        List<CoffeeResponseDto> mockData = List.of(
                new CoffeeResponseDto(1, "맛있는 원두", 30000, 100, "coffee1.jpg"),
                new CoffeeResponseDto(2, "더 맛있는 원두", 45000, 200, "coffee2.jpg")
        );
        given(coffeeService.getCoffees()).willReturn(mockData);

        mockMvc.perform(get("/coffees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("맛있는 원두"))
                .andExpect(jsonPath("$.data[0].price").value(30000));
    }

    @Test
    @DisplayName("GET /coffees - 커피가 없으면 빈 배열 반환")
    void getCoffees_empty() throws Exception {
        given(coffeeService.getCoffees()).willReturn(List.of());

        mockMvc.perform(get("/coffees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }
}

