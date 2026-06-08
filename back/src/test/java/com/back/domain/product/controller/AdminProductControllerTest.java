package com.back.domain.product.controller;

import com.back.domain.product.dto.ProductRequestDto;
import com.back.domain.product.dto.ProductResponseDto;
import com.back.domain.product.entity.Product;
import com.back.domain.product.service.ProductService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminProductControllerTest {
    @Autowired
    private  MockMvc mockMvc;
    @Autowired
    private  ProductService productService;

    @Test
    @DisplayName("POST /admin/products")
    void t1() throws Exception {
        final ResultActions resultActions = mockMvc
                .perform(
                        post("/admin/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "name" : "NEW원두",
                                            "price" : 12000,
                                            "stock" : 300,
                                            "imgUrl" : "product1.jpg"
                                        }
                                        """)
                ).andDo(print());

        final Product product = productService.findLatest().orElseThrow();

        resultActions
                .andExpect(handler().handlerType(AdminProductController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.message").value("상품이 추가되었습니다"))
                .andExpect(jsonPath("$.data.id").value(product.getId()))
                .andExpect(jsonPath("$.data.name").value("NEW원두"))
                .andExpect(jsonPath("$.data.price").value(12000))
                .andExpect(jsonPath("$.data.stock").value(300))
                .andExpect(jsonPath("$.data.imgUrl").value("product1.jpg"))
        ;
    }

    @Test
    @DisplayName("GET /admin/products - 상품 목록 조회 성공")
    void getProducts() throws Exception {
        productService.create("상품 1", 30000, 200, "product1.jpg");
        productService.create("상품 2", 8923, 400, "product2.jpg");

        mockMvc.perform(get("/admin/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("상품 1"))
                .andExpect(jsonPath("$.data[0].price").value(30000));
    }

    @Test
    @DisplayName("GET /admin/products - 상품이 없으면 빈 배열 반환")
    void getProducts_empty() throws Exception {

        mockMvc.perform(get("/admin/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @DisplayName("PATCH /admin/product/{id} - 상품 수정")
    void updateProduct() throws Exception {
        final Product product = productService.create("맛있는 커피", 20000, 10, "coffee.jpg");

        mockMvc.perform(patch("/admin/products/" + product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name" : "더 맛있는 커피",
                                    "price" : 20000,
                                    "stock" : 10,
                                    "imgUrl" : "coffee.jpg"
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-2"))
                .andExpect(jsonPath("$.data.name").value("더 맛있는 커피"));
    }
}


