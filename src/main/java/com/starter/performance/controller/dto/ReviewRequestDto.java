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
    @NotBlank(message = "제목은 필수 항목 입니다.")
    private String reviewTitle;
    @NotBlank(message = "내용은 필수 항목 입니다.")
    @Length(max = 1000)
    private String reviewContent;

//    public Review toEntity() {
////        return Review.builder()
////            .member(Member.builder().memberId(memberId).build())
////            .reservation(Reservation.builder().reservationId(reservationId).build())
////            .reviewTitle(reviewTitle)
////            .reviewContent(reviewContent)
////            .build();
//
//        Member setMember = Member.builder()
//            .memberId(memberId)
//            .build();
//
//        Reservation setReservation = Reservation.builder()
//            .reservationId(reservationId)
//            .build();
//
//        return Review.builder()
//            .member(setMember)
//            .reservation(setReservation)
//            .reviewTitle(reviewTitle)
//            .reviewContent(reviewContent)
//            .build();
//    }

}
