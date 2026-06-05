package com.back.domain.coffee.coffee.dto;

import com.back.domain.coffee.coffee.entity.Coffee;

public class CoffeeResponseDto {
    public record CoffeeResponse(
            Long id,
            String name,
            Integer price,
            Integer stock,
            String imgUrl) {}


}
