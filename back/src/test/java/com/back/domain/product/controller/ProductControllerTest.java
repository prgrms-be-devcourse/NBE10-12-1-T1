package com.back.domain.product.controller;

import com.back.domain.product.entity.Product;
import com.back.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerTest {
    private final MockMvc mockMvc;
    private final ProductRepository productRepository;

    @Autowired
    public ProductControllerTest(MockMvc mockMvc, ProductRepository productRepository) {
        this.mockMvc = mockMvc;
        this.productRepository = productRepository;
    }

    @Test
    @DisplayName("상품 목록 조회 성공")
    void t1() throws Exception {
        Product product1 = productRepository.save(
                new Product("맛있는 원두", 30000, 100, "coffee1.jpg")
        );

        Product product2 = productRepository.save(
                new Product("더 맛있는 원두", 45000, 200, "coffee2.jpg")
        );

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("상품 목록 조회 성공"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))

                .andExpect(jsonPath("$.data[0].id").value(product2.getId()))
                .andExpect(jsonPath("$.data[0].name").value("더 맛있는 원두"))
                .andExpect(jsonPath("$.data[0].price").value(45000))
                .andExpect(jsonPath("$.data[0].stock").value(200))
                .andExpect(jsonPath("$.data[0].imgUrl").value("coffee2.jpg"))

                .andExpect(jsonPath("$.data[1].id").value(product1.getId()))
                .andExpect(jsonPath("$.data[1].name").value("맛있는 원두"))
                .andExpect(jsonPath("$.data[1].price").value(30000))
                .andExpect(jsonPath("$.data[1].stock").value(100))
                .andExpect(jsonPath("$.data[1].imgUrl").value("coffee1.jpg"));
    }

    @Test
    @DisplayName("상품 목록 조회 성공 - 빈 배열 반환")
    void t2() throws Exception {
        productRepository.deleteAll();

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("상품 목록 조회 성공"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @DisplayName("상품 목록 조회 성공 - 소프트 삭제")
    void t3() throws Exception {
        Product product = productRepository.save(
                new Product("맛있는 원두", 30000, 100, "coffee1.jpg")
        );

        product.delete();
        productRepository.save(product);

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("상품 목록 조회 성공"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }
}
