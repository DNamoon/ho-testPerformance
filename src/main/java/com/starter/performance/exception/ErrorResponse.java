package com.starter.performance.exception;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private int statusCode;
    private Object data;

    @Builder
    public ErrorResponse(int statusCode, Object data) {
        this.statusCode = statusCode;
        this.data = data;
    }
}
