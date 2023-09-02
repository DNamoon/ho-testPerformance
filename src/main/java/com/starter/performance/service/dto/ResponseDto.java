package com.starter.performance.service.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseDto {

    private Integer statusCode;
    private String message;

    private Object body;

    @Builder
    public ResponseDto(Integer statusCode, String message, Object body) {
        this.statusCode = statusCode;
        this.message = message;
        this.body = body;
    }
}
