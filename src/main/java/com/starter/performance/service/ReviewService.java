package com.starter.performance.service;

import com.starter.performance.controller.dto.ReviewRequestDto;
import com.starter.performance.domain.PerformanceStatus;
import com.starter.performance.service.dto.ReviewResponseDto;
//import org.springframework.security.core.userdetails.User;

public interface ReviewService {

//    ReviewResponseDto registerReview(ReviewRequestDto reviewDto, Long reservationId, User user);

    String checkCanWriteReview(Long reservation);

    ReviewResponseDto createReview(ReviewRequestDto reviewDto);
}
