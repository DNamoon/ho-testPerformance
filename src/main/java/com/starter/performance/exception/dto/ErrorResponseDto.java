package com.starter.performance.exception.dto;

import com.starter.performance.exception.ErrorData;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponseDto {
    private int statusCode;
    private ErrorData data;

    @Builder
    public ErrorResponseDto(int statusCode, ErrorData data) {
        this.statusCode = statusCode;
        this.data = data;
    }
}
