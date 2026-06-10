package com.back.global.exception;

import com.back.global.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ResponseDto<Void>> handleProductNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseDto<>(
                        "404-1",
                        "상품을 찾을 수 없습니다.",
                        null
                ));
    }
}
