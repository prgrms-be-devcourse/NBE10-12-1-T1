package com.back.domain.product.controller;

import com.back.domain.product.dto.ProductRequestDto.*;
import com.back.domain.product.dto.ProductResponseDto;
import com.back.domain.product.entity.Product;
import com.back.domain.product.service.ProductService;
import com.back.global.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@Tag(name = "관리자 상품 관리", description = "신규 상품 추가 및 상품 목록 조회")

public class AdminProductController {
    private final ProductService productService;

    public record TempResponseDto<T>(
            String resultCode,
            String message,
            T data
    ) {
    }

    // 그 호출이 너무 길어져서 줄여봤습니당 ㅎ ProductRequestDto.*;
    @PostMapping
    @Operation(summary = "관리자 상품 추가")
    private ResponseEntity<TempResponseDto<ProductResponseDto>> create(
            @RequestBody @Valid CreateProductRequest requestDto
    ) {
        //이렇게 쓰는것도 좋은데 예를 들어서 필드 7~8개되면 어떻게 될까용??
        final Product product = productService.create(
                requestDto.name(),
                requestDto.price(),
                requestDto.stock(),
                requestDto.imgUrl()
        );
        return new ResponseEntity<>(
                new TempResponseDto<>(
                        "201-1",
                        "상품이 추가되었습니다",
                        ProductResponseDto.from(product)
                ),
                CREATED
        );
    }
    @GetMapping
    @Operation(summary = "관리자 상품 목록 조회")
    public ResponseEntity<ResponseDto<List<ProductResponseDto>>> getProducts() {
        List<ProductResponseDto> products = productService.getProducts();
        return ResponseEntity.ok(new ResponseDto<>("200", "상품 목록 조회 성공", products));
    }
    @PatchMapping("/{id}")
    private ResponseEntity<ResponseDto<ProductResponseDto>> update(
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
        return ResponseEntity.ok(new ResponseDto<>("200-2", "상품 정보가 수정되었습니다.", ProductResponseDto.from(product)));

    }
}
