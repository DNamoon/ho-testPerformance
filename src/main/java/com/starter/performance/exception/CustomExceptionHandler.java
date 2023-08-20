package com.starter.performance.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice /** exception 핸들러 사용하기 위해서 컨트롤러 레이어에 더 가까이 위치 */
public class CustomExceptionHandler {

    @ExceptionHandler(AbstractException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(AbstractException exception) {
        ErrorResponse errorResponse = ErrorResponse.builder()
            .statusCode(exception.getStatusCode())
            .data(exception.getData())
            .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.resolve(exception.getStatusCode()));
    }
}

