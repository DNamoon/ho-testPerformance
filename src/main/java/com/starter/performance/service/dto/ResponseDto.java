package com.starter.performance.service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseDto {

    private Integer statusCode;
    private String message;
    private Object data;
}
