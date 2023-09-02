package com.starter.performance.exception;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private int statusCode;
    private ErrorData data;

    @Builder
    public ErrorResponse(int statusCode, ErrorData data) {
        this.statusCode = statusCode;
        this.data = data;
    }
}
