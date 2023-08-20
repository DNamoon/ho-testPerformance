package com.starter.performance.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private int statusCode;
    private Object data;

}
