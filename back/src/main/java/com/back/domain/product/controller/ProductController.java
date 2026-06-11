package com.back.domain.product.controller;

import com.back.domain.product.dto.ProductResponseDto;
import com.back.domain.product.service.ProductService;
import com.back.global.annotation.ApiV1;
import com.back.global.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@ApiV1
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "ProductController", description = "사용자 상품 컨트롤러")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "관리자 사용자 상품 목록 조회")
    public ResponseDto<List<ProductResponseDto>> getProducts() {
        List<ProductResponseDto> products = productService.getProducts();

        return new ResponseDto<>(
                "200-1",
                "관리자 상품 목록 조회 성공",
                products
        );
    }
}
