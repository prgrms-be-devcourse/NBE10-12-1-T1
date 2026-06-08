package com.back.domain.coffee.coffee.dto;

import com.back.domain.coffee.coffee.entity.Coffee;
import jakarta.validation.constraints.NotNull;

public record CoffeeResponseDto (
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
    public static CoffeeResponseDto from(Coffee coffee) {
        return new CoffeeResponseDto(
                coffee.getId(),
                coffee.getName(),
                coffee.getPrice(),
                coffee.getStock(),
                coffee.getImgUrl()
        );
    }
}

