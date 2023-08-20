package com.starter.performance.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class ReviewResponseDto {

    private String message;
    private String reviewTitle;
    private String reviewContent;

}
