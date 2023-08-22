package com.starter.performance.controller;

import com.starter.performance.controller.dto.ReviewRequestDto;
import com.starter.performance.exception.ReviewErrorCode;
import com.starter.performance.service.ReservationService;
import com.starter.performance.service.ReviewService;
import com.starter.performance.service.dto.ReviewResponseDto;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.User;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/review")
    public ResponseEntity<?> createReview(@RequestBody @Valid ReviewRequestDto reviewDto){

        ReviewResponseDto reviewResponseDto = reviewService.createReview(reviewDto);

        return ResponseEntity.ok(reviewResponseDto);
    }


    /** 시큐리티 끄고 해야함 - 토큰에서 정보를 유저 정보와, reservationId를 받아온다는 가정*/
//    //'application/x-www-form-urlencoded;charset=UTF-8' not supported 에러 발생 -> @RequestBody 어노테이션 삭제
//    // https://irerin07.tistory.com/116
//    @PostMapping("/review/{reservationId}")
//    public ResponseEntity<?> createReview(@Valid ReviewRequestDto reviewRequestDto,
//        @PathVariable Long reservationId,@AuthenticationPrincipal User user,
//        Errors errors) {
//
//        reviewService.registerReview(reviewRequestDto, reservationId, user);
//
//        return ResponseEntity.ok("ok");
//    }

//    @PostMapping("/review")
//    public ResponseEntity<ReviewResponseDto> createReview(@RequestBody @Valid ReviewRequestDto reviewDto,
//        @AuthenticationPrincipal User user,
//        @RequestParam("reservationId") Long reservationId, Errors errors) {
//
//        //user.getUsername()을 통해서 토큰에 저장된 memberId를 불러올 수 있는데 이 때, 무조건 String인 듯.
//        ReviewResponseDto reviewResponseDto = reviewService.registerReview(reviewDto, user.getClass(), reservationId);
//
//        return ResponseEntity.ok(reviewResponseDto);
//    }

}