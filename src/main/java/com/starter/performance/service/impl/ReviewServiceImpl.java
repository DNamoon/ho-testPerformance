package com.starter.performance.service.impl;

import com.starter.performance.controller.dto.ReviewRequestDto;
import com.starter.performance.domain.Member;
import com.starter.performance.domain.PerformanceStatus;
import com.starter.performance.domain.Reservation;
import com.starter.performance.domain.Review;
import com.starter.performance.exception.impl.CanNotWriteReviewException;
import com.starter.performance.exception.impl.NotMatchReservationAndMemberException;
import com.starter.performance.exception.impl.OnlyOneReviewException;
import com.starter.performance.repository.MemberRepository;
import com.starter.performance.repository.ReservationRepository;
import com.starter.performance.repository.ReviewRepository;
import com.starter.performance.service.ReviewService;
import com.starter.performance.service.dto.ResponseDto;
import com.starter.performance.service.dto.ReviewResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;

    private static final String REVIEW_MESSAGE = "후기 작성을 완료했습니다.";

//    @Override
//    public ResponseDto createReview(ReviewRequestDto reviewDto) {
//
//        Member member = memberRepository.findById(reviewDto.getMemberId())
//            .orElseThrow(() -> new IllegalArgumentException());
//        Reservation reservation = reservationRepository.findById(reviewDto.getReservationId())
//            .orElseThrow(() -> new IllegalArgumentException());
//
//        // 예매 한 번 당 후기 한 번 확인
//        checkOneReservation(member, reservation);
//
//        // 리뷰 작성 가능한 상태인지 확인
//        checkCanWriteReview(reservation);
//
//        Review review = Review.builder()
//            .member(member)
//            .reservation(reservation)
//            .title(reviewDto.getReviewTitle())
//            .content(reviewDto.getReviewContent())
//            .build();
//
//        Review savedReview = reviewRepository.save(review);
//
//        return ResponseDto.builder()
//            .message(MESSAGE)
//            .statusCode(String.valueOf(HttpStatus.OK))
//            .body(ReviewResponseDto.builder().title(savedReview.getTitle()).build())
//            .build();
//    }

    @Transactional
    @Override
    public ResponseDto registerReviewV2(ReviewRequestDto reviewDto, Long reservationId, Authentication auth) {

        log.info("auth.getName() : " + auth.getName());
        log.info("auth.getPrincipal() : " + auth.getPrincipal().toString());

        String email = auth.getName();
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(IllegalArgumentException::new);

        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(IllegalArgumentException::new);

        // 리뷰 작성 회원과 예매 정보 회원정보 일치 확인
        checkReservationInfo(member, reservation);
        // 예매 한 번 당 후기 한 번 확인
        checkOneReservation(member, reservation);
        // 리뷰 작성 가능한 상태인지 확인
        checkCanWriteReview(reservation);

        Review review = Review.builder()
            .member(member)
            .reservation(reservation)
            .title(reviewDto.getReviewTitle())
            .content(reviewDto.getReviewContent())
            .build();

        Review savedReview = reviewRepository.save(review);

        return ResponseDto.builder()
            .statusCode(HttpStatus.OK.value())
            .message(REVIEW_MESSAGE)
            .body(ReviewResponseDto.builder().title(savedReview.getTitle()).build())
            .build();
    }

    //리뷰 작성 가능한 상태인지 확인하는 메서드. - performanceStatus 확인
    @Override
    public void checkCanWriteReview(Reservation reservation) {
        if (!reservation.getPerformanceSchedule().getPerformanceStatus().equals(PerformanceStatus.END)) {
            throw new CanNotWriteReviewException();
        }
    }

    // 예매 한 번 당 후기는 한 번 작성할 수 있다. - review 테이블에 똑같은 reservation_id, member_id가 있는지 체크
    @Override
    public void checkOneReservation(Member member, Reservation reservation) {
        if (reviewRepository.findByReservationAndMember(reservation, member).isPresent()) {
            throw new OnlyOneReviewException();
        }
    }

    // 후기 작성중인 회원정보와 예매 정보의 회원정보 일치 확인
    @Override
    public void checkReservationInfo(Member member, Reservation reservation) {
        if (!reservation.getMember().equals(member)) {
            throw new NotMatchReservationAndMemberException();
        }
    }

}
