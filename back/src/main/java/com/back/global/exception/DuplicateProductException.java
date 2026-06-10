package com.back.global.exception;

public class DuplicateProductException extends RuntimeException {
    
    public DuplicateProductException() {
        super("주문 상품 목록에 중복된 상품이 존재합니다.");
    }
}