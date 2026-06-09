package com.back.domain.product.controller;

import com.back.domain.product.dto.ProductResponseDto;
import com.back.domain.product.service.ProductService;
import com.back.global.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "ProductController", description = "사용자 상품 컨트롤러")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "사용자 상품 목록 조회")
    public ResponseDto<List<ProductResponseDto>> getProducts() {
        List<ProductResponseDto> products = productService.getProducts();
        // ResponseDto 내부에 int status --> String resultCode로 변경에 따른 수정
        return new ResponseDto<>("200-1", "상품 목록 조회 성공", products);
    }
}
