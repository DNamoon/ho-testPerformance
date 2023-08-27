package com.starter.performance.exception.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorResponseDto {

    private final String statusCode;
    private final ErrorDataDto data;
}