package com.back.global.dto;

public record ResponseDto<T>(int status, String message, T data) {
}
