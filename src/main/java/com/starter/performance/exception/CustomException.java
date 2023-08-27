package com.starter.performance.exception;

import com.starter.performance.exception.dto.ErrorResponseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {

    private final ErrorResponseDto errorResponseDto;
    private final HttpStatus httpStatus;
}