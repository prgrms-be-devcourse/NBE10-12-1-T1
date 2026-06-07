package com.back.domain.coffee.coffee.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CoffeeRequestDto {
    public record CreateCoffeeRequest(
            @NotBlank(message = "커피 이름은 필수입니다")
            String name,
            @NotNull(message = "가격은 필수입니다")
            Integer price,
            @NotNull(message = "초기 재고는 필수입니다.")
            @Min(value = 0, message = "재고는 0개 이상이어야 합니다.")
            Integer stock,

            String imgUrl
    ) {
    }

    public record PatchCoffeeRequest(
            @Pattern(regexp = ".*\\S.*", message = "이름을 빈칸으로 수정할 수 없습니다.")
            String name,
            @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
            Integer price,
            @Min(value = 0, message = "재고는 0개 이상이어야 합니다.")
            Integer stock,

            String imgUrl
    ) {
    }
}

