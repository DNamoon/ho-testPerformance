package com.starter.performance.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ShowReviewsDto {

    private String performanceName;
    private String title;
    private String content;
    private String writer;
    private String reviewStatus;
    private LocalDateTime writingDate;
    private LocalDateTime deleteDate;

    @Builder
    public ShowReviewsDto(String performanceName, String title, String content, String writer, String reviewStatus,
        LocalDateTime writingDate, LocalDateTime deleteDate) {
        this.performanceName = performanceName;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.reviewStatus = reviewStatus;
        this.writingDate = writingDate;
        this.deleteDate = deleteDate;
    }
}
