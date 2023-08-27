package com.starter.performance.service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseDto {
    String statusCode;
    String message;
    Object data;
}
