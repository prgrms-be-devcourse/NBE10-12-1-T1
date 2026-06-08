package com.back.domain.product.controller;

import com.back.domain.product.dto.ProductResponseDto;
import com.back.domain.product.service.ProductService;
import com.back.global.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ResponseDto<List<ProductResponseDto>>> getProducts() {
        List<ProductResponseDto> products = productService.getProducts();
        // ResponseDto 내부에 int status --> String resultCode로 변경에 따른 수정
        return ResponseEntity.ok(new ResponseDto<>("200-1", "상품 목록 조회 성공", products));
    }
}
