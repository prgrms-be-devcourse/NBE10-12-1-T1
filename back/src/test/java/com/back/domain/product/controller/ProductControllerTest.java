package com.back.domain.product.controller;


import com.back.domain.product.dto.ProductResponseDto;
import com.back.domain.product.service.ProductService;
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


@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    ProductService productService;

    @Test
    @DisplayName("GET /products - 상품 목록 조회 성공")
    void t1() throws Exception {
        List<ProductResponseDto> mockData = List.of(
                new ProductResponseDto(1L, "맛있는 원두", 30000, 100, "coffee1.jpg"),
                new ProductResponseDto(2L, "더 맛있는 원두", 45000, 200, "coffee2.jpg")
        );

        given(productService.getProducts()).willReturn(mockData);

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("상품 목록 조회 성공"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))

                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("맛있는 원두"))
                .andExpect(jsonPath("$.data[0].price").value(30000))
                .andExpect(jsonPath("$.data[0].stock").value(100))
                .andExpect(jsonPath("$.data[0].imgUrl").value("coffee1.jpg"))

                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].name").value("더 맛있는 원두"))
                .andExpect(jsonPath("$.data[1].price").value(45000))
                .andExpect(jsonPath("$.data[1].stock").value(200))
                .andExpect(jsonPath("$.data[1].imgUrl").value("coffee2.jpg"));
    }

    @Test
    @DisplayName("GET /products - 상품이 없으면 빈 배열 반환")
    void t2() throws Exception {
        given(productService.getProducts()).willReturn(List.of());

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("상품 목록 조회 성공"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }
}
