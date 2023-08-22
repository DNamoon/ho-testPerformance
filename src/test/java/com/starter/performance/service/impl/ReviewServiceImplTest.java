package com.starter.performance.service.impl;

import com.starter.performance.controller.dto.ReviewRequestDto;
import com.starter.performance.domain.Member;
import com.starter.performance.domain.PerformanceSchedule;
import com.starter.performance.domain.Reservation;
import com.starter.performance.domain.Review;
import com.starter.performance.exception.impl.CanNotWriteReviewException;
import com.starter.performance.exception.impl.OnlyOneReviewException;
import com.starter.performance.repository.MemberRepository;
import com.starter.performance.repository.ReservationRepository;
import com.starter.performance.repository.ReviewRepository;
import com.starter.performance.service.dto.ReviewResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest{

    @InjectMocks
   private ReviewServiceImpl reviewService;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ReservationRepository reservationRepository;


    @Test
    @DisplayName("성공 test")
    void createReviewTest1(){
        ReviewRequestDto reviewRequestDto = ReviewRequestDto.builder()
                                                .memberId(1L)
                                                .reservationId(1L)
                                                .reviewTitle("제목")
                                                .reviewContent("sodyd")
                                                .build();
        Member member = Member.builder()
                .memberId(1L)
                .build();

        PerformanceSchedule performanceSchedule = PerformanceSchedule.builder()
                .performanceStatus("END")
                .build();

        Reservation reservation = Reservation.builder()
                .reservationId(1L)
                .performanceSchedule(performanceSchedule)
                .build();

        Review review = Review.builder()
                .reviewTitle("제목")
                .build();

        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(reservationRepository.findById(any())).thenReturn(Optional.of(reservation));
        when(reviewRepository.findByReservationAndMember(any(), any())).thenReturn(Optional.empty());
        when(reviewRepository.save(any())).thenReturn(review);

        ReviewResponseDto reviewResponseDto = reviewService.createReview(reviewRequestDto);

        assertEquals(reviewResponseDto.getReviewTitle(), "제목");

    }

    @Test
    @DisplayName("END 가 아닌 경우")
    void createReviewTest2(){
        ReviewRequestDto reviewRequestDto = ReviewRequestDto.builder()
                                                .memberId(1L)
                                                .reservationId(1L)
                                                .reviewTitle("제목")
                                                .reviewContent("sodyd")
                                                .build();
        Member member = Member.builder()
                .memberId(1L)
                .build();

        PerformanceSchedule performanceSchedule = PerformanceSchedule.builder()
                .performanceStatus("STANDBY")
                .build();

        Reservation reservation = Reservation.builder()
                .reservationId(1L)
                .performanceSchedule(performanceSchedule)
                .build();

        Review review = Review.builder()
                .reviewTitle("제목")
                .build();

        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(reservationRepository.findById(any())).thenReturn(Optional.of(reservation));
        when(reviewRepository.findByReservationAndMember(any(), any())).thenReturn(Optional.empty());

        assertThrows(CanNotWriteReviewException.class, () -> reviewService.createReview(reviewRequestDto));
//        CanNotWriteReviewException canNotWriteReviewException = assertThrows(CanNotWriteReviewException.class, () -> reviewService.createReview(reviewRequestDto));

    }

    @Test
    @DisplayName("예매 1건 이상")
    void createReviewTest3(){
        ReviewRequestDto reviewRequestDto = ReviewRequestDto.builder()
                                                .memberId(1L)
                                                .reservationId(1L)
                                                .reviewTitle("제목")
                                                .reviewContent("sodyd")
                                                .build();
        Member member = Member.builder()
                .memberId(1L)
                .build();

        PerformanceSchedule performanceSchedule = PerformanceSchedule.builder()
                .performanceStatus("END")
                .build();

        Reservation reservation = Reservation.builder()
                .reservationId(1L)
                .performanceSchedule(performanceSchedule)
                .build();

        Review review = Review.builder()
                .reviewTitle("제목")
                .build();

        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(reservationRepository.findById(any())).thenReturn(Optional.of(reservation));
        when(reviewRepository.findByReservationAndMember(any(), any())).thenReturn(Optional.of(review));

        assertThrows(OnlyOneReviewException.class, () -> reviewService.createReview(reviewRequestDto));

    }

}