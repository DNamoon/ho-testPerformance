package com.starter.performance.service;

import com.starter.performance.controller.dto.ReviewRequestDto;
import com.starter.performance.domain.Member;
import com.starter.performance.domain.Reservation;
import com.starter.performance.service.dto.ResponseDto;
import org.springframework.security.core.Authentication;

public interface ReviewService {

    //    ResponseDto createReview(ReviewRequestDto reviewDto);

    void checkCanWriteReview(Reservation reservation);

    void checkOneReservation(Member member, Reservation reservation);

    void checkReservationInfo(Member member, Reservation reservation);

    ResponseDto registerReviewV2(ReviewRequestDto reviewDto, Long reservationId, Authentication auth);
}
