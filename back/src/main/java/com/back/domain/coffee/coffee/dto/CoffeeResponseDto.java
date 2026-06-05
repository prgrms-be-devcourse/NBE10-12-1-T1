package com.back.domain.coffee.coffee.dto;

import com.back.domain.coffee.coffee.entity.Coffee;
import jakarta.validation.constraints.NotNull;

public class CoffeeResponseDto {
    public record CoffeeResponse(
            @NotNull
            Long id,
            @NotNull
            String name,
            @NotNull
            Integer price,
            @NotNull
            Integer stock,
            String imgUrl) {}


}
