package com.back.domain.product.controller;

import com.back.domain.product.dto.ProductRequestDto.*;
import com.back.domain.product.dto.ProductResponseDto;
import com.back.domain.product.entity.Product;
import com.back.domain.product.service.ProductService;
import com.back.global.annotation.ApiV1;
import com.back.global.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiV1
@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@Tag(name = "관리자 상품 관리", description = "신규 상품 추가 및 상품 목록 조회")

public class AdminProductController {
    private final ProductService productService;

    // 그 호출이 너무 길어져서 줄여봤습니당 ㅎ ProductRequestDto.*;
    @PostMapping
    @Operation(summary = "관리자 상품 추가")
    public ResponseDto<ProductResponseDto> create(
            @RequestBody @Valid CreateProductRequest requestDto
    ) {
        //이렇게 쓰는것도 좋은데 예를 들어서 필드 7~8개되면 어떻게 될까용??
        final Product product = productService.create(
                requestDto.name(),
                requestDto.price(),
                requestDto.stock(),
                requestDto.imgUrl()
        );
        return new ResponseDto<>(
                        "201-1",
                        "상품 생성 성공",
                        ProductResponseDto.from(product)
        );
    }

    @GetMapping
    @Operation(summary = "관리자 상품 목록 조회")
    public ResponseDto<List<ProductResponseDto>> getProducts() {
        List<ProductResponseDto> products = productService.getProducts();
        return new ResponseDto<>("200", "상품 목록 조회 성공", products);
    }

    @PatchMapping("/{id}")
    public ResponseDto<ProductResponseDto> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid PatchProductRequest requestDto
    ) {
        final Product product = productService.update(
                id,
                requestDto.name(),
                requestDto.price(),
                requestDto.stock(),
                requestDto.imgUrl()
        );
        return new ResponseDto<>("200-1", "상품 수정 성공", ProductResponseDto.from(product));

    }

    @DeleteMapping("/{id}")
    @Operation(summary = "관리자 상품 제거")
    public ResponseDto<Void> delete(@PathVariable Long id) {
        Product product = productService.findById(id).get();

        productService.delete(product);

        return new ResponseDto<>(
                        "200-1",
                        "상품이 삭제되었습니다",
                        null
        );
    }
}
