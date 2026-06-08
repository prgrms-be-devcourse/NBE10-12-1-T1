package com.back.domain.product.dto;

import com.back.domain.product.entity.Product;
import jakarta.validation.constraints.NotNull;

public record ProductResponseDto(
        @NotNull
        Long id,
        @NotNull
        String name,
        @NotNull
        Integer price,
        @NotNull
        Integer stock,
        String imgUrl
) {
    public static ProductResponseDto from(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getImgUrl()
        );
    }
}

