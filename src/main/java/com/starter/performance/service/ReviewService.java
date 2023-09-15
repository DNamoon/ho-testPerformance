package com.starter.performance.service;

import com.starter.performance.controller.dto.ReviewRequestDto;
import com.starter.performance.domain.Member;
import com.starter.performance.domain.Reservation;
import com.starter.performance.controller.dto.ResponseDto;
import com.starter.performance.domain.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface ReviewService {

    //    ResponseDto createReview(ReviewRequestDto reviewDto);

    // 삭제된 후기인지 확인
    void checkDeleteReview(Review review);

    // 로그인 회원과 예매를 작성한 회원이 일치하는지 확인
    void checkReviewMemberMatch(Review review, Member member);

    void checkCanWriteReview(Reservation reservation);

    void checkOneReservation(Member member, Reservation reservation);

    void checkReservationInfo(Member member, Reservation reservation);

    ResponseDto registerReview(ReviewRequestDto reviewDto, Long reservationId, Authentication auth);

    ResponseDto showReviews(Authentication auth, Pageable pageable);

    ResponseDto deleteReview(Authentication auth, Long reviewId);

    ResponseDto changeReview(ReviewRequestDto dto, Long reviewId, Authentication auth);
}
