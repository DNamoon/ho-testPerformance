package com.starter.performance.service.impl;

import com.starter.performance.controller.dto.ReviewRequestDto;
import com.starter.performance.domain.Member;
import com.starter.performance.domain.PerformanceStatus;
import com.starter.performance.domain.Reservation;
import com.starter.performance.domain.Review;
import com.starter.performance.exception.Data;
import com.starter.performance.exception.impl.CanNotWriteReviewException;
import com.starter.performance.exception.impl.OnlyOneReviewException;
import com.starter.performance.repository.MemberRepository;
import com.starter.performance.repository.ReservationRepository;
import com.starter.performance.repository.ReviewRepository;
import com.starter.performance.service.ReviewService;
import com.starter.performance.service.dto.ReviewResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public ReviewResponseDto createReview(ReviewRequestDto reviewDto) {

        Member member = memberRepository.findById(reviewDto.getMemberId()).orElseThrow(()-> new IllegalArgumentException());
        Reservation reservation = reservationRepository.findById(reviewDto.getReservationId()).orElseThrow(()-> new IllegalArgumentException());

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

        return ReviewResponseDto.builder()
            .StatusCode(HttpStatus.OK.value())
            .data(Data.builder().reviewTitle(savedReview.getTitle()))
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


    //Controller도 createReview , 서비스 메서드도 createReview 인 건 문제가 있을 것 같습니다.
//    @Override
//    public ReviewResponseDto registerReview(ReviewRequestDto reviewDto, Long reservationId, User user) {
//
//        log.info("여기는 넘어오는것인가?");
////          /** 방법 1 : 후기 작성 가능 기간 에러 - JPQL 방법 */
////        if (!checkCanWriteReview(reservationId).equals(PerformanceStatus.END)) {
////            throw new RuntimeException("현재 후기를 작성할 수 없습니다.");
////        }
//
//        /** 방법 2 : 후기 작성 가능 기간 에러 - findBy 외래키 조회 방법 https://pooney.tistory.com/83  */
//        //잘못된 방법? findByPerformanceSchedule이 공연일정 아이디를 반환하는게 아닌 것 같은데?
////        Long performanceScheduleId = reservationRepository.findByPerformanceSchedule(
////            reservationId);
//        Optional<Reservation> reservation = reservationRepository.findById(reservationId);
//
//        PerformanceSchedule performanceSchedule = reservation.get().getPerformanceSchedule();
//
//        log.info("2222여기는 넘어오는것인가?");
//
//
//        //외래키 조인 다시 공부해야 할 듯.
//        if (!reservationRepository.findByPerformanceSchedule_PerformanceStatus
//                (performanceSchedule.getPerformanceScheduleId())
//            .equals("END")) {
//            throw new RuntimeException("현재 후기를 작성할 수 없습니다.");
//        }
//
//        //getUsername() 이 username으로 일반적인 아이디를 뜻한다. 우리 서비스는 이메일을 아이디로 사용하니, 이메일을 의미할 것이다.
//        //실제 로그인 되어 있지 않아서 토큰 존재 x.
//        Member setMember = memberRepository.findByEmail(user.getUsername());
//        Reservation setReservation = reservationRepository.findByReservationId(reservationId);
//
//        Review review = Review.builder()
//            .member(setMember)
//            .reservation(setReservation)
//            .reviewTitle(reviewDto.getReviewTitle())
//            .reviewContent(reviewDto.getReviewContent())
//            .build();
//
//        Review savedReview = reviewRepository.save(review);
//
//        return ReviewResponseDto.builder()
//            .message("후기 작성을 완료했습니다.")
//            .reviewTitle(savedReview.getReviewTitle())
//            .reviewContent(savedReview.getReviewContent())  // 굳이 내용까지 반환해야하나? 라는 생각이 드네요.
//            .build();
//    }

}
