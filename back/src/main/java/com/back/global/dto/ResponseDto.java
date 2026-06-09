package com.back.global.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record ResponseDto<T>(String resultCode, @JsonIgnore int statusCode, String message, T data) {
    public ResponseDto(String resultCode, String msg) {
        this(resultCode, msg, null);
    }
    public ResponseDto(String resultCode, String msg, T data) {
        this(resultCode, Integer.parseInt(resultCode.split("-", 2)[0]), msg, data);
    }
    }
