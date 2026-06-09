package com.back.domain.product.controller;

import com.back.domain.product.entity.Product;
import com.back.domain.product.service.ProductService;
import org.junit.jupiter.api.Assertions;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("관리자 상품 생성")
    void t1() throws Exception {
        final ResultActions resultActions = mockMvc
                .perform(
                        post("/api/v1/admin/products")
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

        final Product product = productService.findLatest().orElseThrow();

        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.message").value("상품 생성 성공"))
                .andExpect(jsonPath("$.data.id").value(product.getId()))
                .andExpect(jsonPath("$.data.name").value("NEW원두"))
                .andExpect(jsonPath("$.data.price").value(12000))
                .andExpect(jsonPath("$.data.stock").value(300))
                .andExpect(jsonPath("$.data.imgUrl").value("coffee1.jpg"))
        ;
    }

    @Test
    @DisplayName("관리자 상품 목록 조회 성공")
    void t2() throws Exception {
        productService.create("맛있는 원두", 30000, 200, "coffee1.jpg");
        productService.create("더 맛있는 원두", 45000, 400, "coffee2.jpg");

        mockMvc.perform(get("/api/v1/admin/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("맛있는 원두"))
                .andExpect(jsonPath("$.data[0].price").value(30000))
                .andExpect(jsonPath("$.data[0].stock").value(200))
                .andExpect(jsonPath("$.data[0].imgUrl").value("coffee1.jpg"))

                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].name").value("더 맛있는 원두"))
                .andExpect(jsonPath("$.data[1].price").value(45000))
                .andExpect(jsonPath("$.data[1].stock").value(400))
                .andExpect(jsonPath("$.data[1].imgUrl").value("coffee2.jpg"));
    }

    @Test
    @DisplayName("관리자 상품 목록 조회 성공 - 빈 배열 반환")
    void t3() throws Exception {

        mockMvc.perform(get("/api/v1/admin/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @DisplayName("관리자 상품 수정 성공")
    void updateProduct() throws Exception {
        final Product product = productService.create("맛있는 커피", 20000, 10, "coffee.jpg");

        mockMvc.perform(patch("/api/v1/admin/products/" + product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name" : "더 맛있는 커피",
                                    "price" : 25000,
                                    "stock" : 100,
                                    "imgUrl" : "coffee2.jpg"
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("상품 수정 성공"))
                .andExpect(jsonPath("$.data.id").value(product.getId()))
                .andExpect(jsonPath("$.data.name").value("더 맛있는 커피"))
                .andExpect(jsonPath("$.data.price").value(25000))
                .andExpect(jsonPath("$.data.stock").value(100))
                .andExpect(jsonPath("$.data.imgUrl").value("coffee2.jpg"));

    }

    @Test
    @DisplayName("관리자 상품 삭제(소프트 삭제) 성공")
    void deleteProducts() throws Exception {
        Product product1 = productService.create("상품 1", 30000, 200, "product1.jpg");

        mockMvc.perform(delete("api/v1/admin/products/{id}", product1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("상품이 삭제되었습니다"))
                .andExpect(jsonPath("$.data").doesNotExist());

        Product product = productService.findById(product1.getId()).get();
        Assertions.assertNotNull(product.getDeletedAt());
    }
}


