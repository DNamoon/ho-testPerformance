package com.starter.performance.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdateReviewDto {

    private String performanceName;
    private String title;
    private String writer;
    private String reviewStatus;
    private LocalDateTime writingDate;

    @Builder
    public UpdateReviewDto(String performanceName, String title, String writer, String reviewStatus,
        LocalDateTime writingDate) {
        this.performanceName = performanceName;
        this.title = title;
        this.writer = writer;
        this.reviewStatus = reviewStatus;
        this.writingDate = writingDate;
    }
}
