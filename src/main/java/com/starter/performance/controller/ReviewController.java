package com.starter.performance.controller;

import com.starter.performance.controller.dto.ReviewRequestDto;
import com.starter.performance.service.ReviewService;
import com.starter.performance.controller.dto.ResponseDto;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

//    @PostMapping("/review")
//    public ResponseEntity<?> createReview(@RequestBody @Valid ReviewRequestDto reviewDto) {
//
//        ResponseDto reviewResponseDto = reviewService.createReview(reviewDto);
//
//        return ResponseEntity.ok(reviewResponseDto);
//    }

    // 토큰에서 로그인한 회원 정보(pk)와 예약 내역 페이지에서 reservationId를 받아와 후기 작성 요청
//    @PostMapping("/reservations/reviews/{reservationId}")
//    public ResponseEntity<?> createReview(@RequestBody @Valid ReviewRequestDto reviewRequestDto,
//        @PathVariable Long reservationId, @AuthenticationPrincipal User user) {
//
//        ResponseDto responseDto = reviewService.registerReview(reviewRequestDto, reservationId, user);
//
//        return ResponseEntity.ok(responseDto);
//    }

    //Eunjeong21님 방법 - 이게 더 올바른 코드인 듯.
    //예매하기
    @PostMapping("/reservations/reviews/{reservationId}")
    public ResponseEntity<?> createReviewV2(@RequestBody @Valid ReviewRequestDto reviewRequestDto,
        @PathVariable Long reservationId, Authentication auth) {

        ResponseDto responseDto = reviewService.registerReview(reviewRequestDto, reservationId, auth);

        return ResponseEntity.ok(responseDto);
    }

    // 예매 목록 보기
    @GetMapping("/reservations/reviews")
    public ResponseEntity<ResponseDto> viewReviews(Authentication auth, Pageable pageable) {
        ResponseDto responseDto = reviewService.showReviews(auth, pageable);
        return ResponseEntity.ok(responseDto);
    }

    // 예매 삭제
    @DeleteMapping("/reservations/reviews/{reviewId}")
    public ResponseEntity<ResponseDto> deleteReview(Authentication auth,
        @PathVariable Long reviewId) {
        ResponseDto responseDto = reviewService.deleteReview(auth, reviewId);
        return ResponseEntity.ok(responseDto);
    }

    // 예매 수정
    @PutMapping("/reservations/reviews/{reviewId}")
    public ResponseEntity<ResponseDto> changeReview(@RequestBody @Valid ReviewRequestDto dto,
        @PathVariable Long reviewId, Authentication auth) {
        ResponseDto responseDto = reviewService.changeReview(dto, reviewId, auth);
        return ResponseEntity.ok(responseDto);
    }

}
