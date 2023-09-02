package com.starter.performance.exception;

import com.starter.performance.exception.dto.ErrorResponseDto;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice /** exception 핸들러 사용하기 위해서 컨트롤러 레이어에 더 가까이 위치 */
public class CustomExceptionHandler {

    @ExceptionHandler(AbstractException.class)
    protected ResponseEntity<ErrorResponseDto> handleCustomException(AbstractException exception) {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
            .statusCode(exception.getStatusCode())
            .data(exception.getData())
            .build();

        return new ResponseEntity<>(errorResponseDto,
            Objects.requireNonNull(HttpStatus.resolve(exception.getStatusCode())));
    }
}

