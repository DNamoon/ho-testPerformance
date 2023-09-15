package com.starter.performance.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ReviewRequestDto {

//    @NotNull
//    private Long memberId;
//    @NotNull
//    private Long reservationId;
    @NotBlank(message = "제목은 필수 항목입니다.")
    private String reviewTitle;
    @NotBlank(message = "내용은 필수 항목입니다.")
    @Size(max = 1000)
    private String reviewContent;

}
