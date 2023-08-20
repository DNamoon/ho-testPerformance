package com.starter.performance.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Data {
    private ReviewErrorCode errorType;
    private String errorMessage;
}
