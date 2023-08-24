package com.starter.performance.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Data {
    private ReviewErrorCode errorType;
    private String reviewTitle;
}
