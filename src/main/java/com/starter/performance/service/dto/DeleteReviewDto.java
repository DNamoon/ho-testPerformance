package com.starter.performance.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DeleteReviewDto {
    private String performanceName;
    private String title;
    private String writer;
    private String reviewStatus;
    private LocalDateTime deleteDate;

    @Builder
    public DeleteReviewDto(String performanceName, String title, String writer, String reviewStatus,
        LocalDateTime deleteDate) {
        this.performanceName = performanceName;
        this.title = title;
        this.writer = writer;
        this.reviewStatus = reviewStatus;
        this.deleteDate = deleteDate;
    }
}
