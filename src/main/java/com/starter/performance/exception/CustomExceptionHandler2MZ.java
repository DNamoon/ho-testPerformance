package com.starter.performance.exception;

import com.starter.performance.exception.dto.ErrorResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler2MZ {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseDto> handleCustomException(CustomException e) {
        return new ResponseEntity<>(
            e.getErrorResponseDto(), e.getHttpStatus()
        );
    }
}