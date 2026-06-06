package com.back.global.dto;

public record ResponseDto<T>(String resultCode, String message, T data) {
}
