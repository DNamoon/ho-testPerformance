package com.starter.performance.service.dto;

import com.starter.performance.exception.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;

@Setter
@Builder
@AllArgsConstructor
public class ReviewResponseDto {

    private int StatusCode;
    private Object data;

}
