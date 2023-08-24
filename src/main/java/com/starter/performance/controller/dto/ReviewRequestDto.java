package com.starter.performance.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ReviewRequestDto {

    @NotNull
    private Long memberId;
    @NotNull
    private Long reservationId;
    @NotBlank(message = "제목은 필수 항목입니다.")
    private String reviewTitle;
    private String reviewContent;

}
