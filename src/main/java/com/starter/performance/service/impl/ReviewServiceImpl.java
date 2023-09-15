package com.starter.performance.service.impl;

import com.starter.performance.controller.dto.ReviewRequestDto;
import com.starter.performance.domain.Member;
import com.starter.performance.domain.PerformanceStatus;
import com.starter.performance.domain.Reservation;
import com.starter.performance.domain.Review;
import com.starter.performance.domain.ReviewStatus;
import com.starter.performance.exception.impl.CanNotWriteReviewException;
import com.starter.performance.exception.impl.DeletedReviewException;
import com.starter.performance.exception.impl.NotExistSuitableDataException;
import com.starter.performance.exception.impl.NotMatchReservationAndMemberException;
import com.starter.performance.exception.impl.OnlyOneReviewException;
import com.starter.performance.repository.MemberRepository;
import com.starter.performance.repository.ReservationRepository;
import com.starter.performance.repository.ReviewRepository;
import com.starter.performance.service.ReviewService;
import com.starter.performance.controller.dto.ResponseDto;
import com.starter.performance.service.dto.DeleteReviewDto;
import com.starter.performance.service.dto.ReviewResponseDto;
import com.starter.performance.service.dto.ShowReviewsDto;
import com.starter.performance.service.dto.UpdateReviewDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private static final String SHOW_MESSAGE = "작성한 후기들을 불러옵니다.";
    private static final String CANCEL_MESSAGE = "해당 후기를 삭제합니다.";
    private static final String CHANGE_MESSAGE = "해당 후기를 수정합니다.";
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
    public ResponseDto registerReview(ReviewRequestDto reviewDto, Long reservationId, Authentication auth) {

        log.info("auth.getRatingName() : " + auth.getName());
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

    @Override
    public ResponseDto showReviews(Authentication auth, Pageable pageable) {
        List<ShowReviewsDto> dtoList = new ArrayList<>();

        String email = auth.getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(NotExistSuitableDataException::new);

        Page<Review> list = reviewRepository.findAllByMember(member, pageable);

        for (Review savedReview : list) {
            ShowReviewsDto dto = ShowReviewsDto.builder()
                .performanceName(savedReview.getReservation().getPerformanceSchedule().getPerformance().getName())
                .title(savedReview.getTitle())
                .content(savedReview.getContent())
                .writer(savedReview.getMember().getNickname())
                .reviewStatus(savedReview.getReviewStatus().name())
                .writingDate(savedReview.getWritingDate())
                .deleteDate(savedReview.getDeleteDate())
                .build();

            dtoList.add(dto);
        }

        return ResponseDto.builder()
            .statusCode(HttpStatus.OK.value())
            .message(SHOW_MESSAGE)
            .body(dtoList)
            .build();
    }

    @Transactional
    @Override
    public ResponseDto deleteReview(Authentication auth, Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(NotExistSuitableDataException::new);
        String email = auth.getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(NotExistSuitableDataException::new);

        /** 후기 정보의 회원정보와 로그인 회원 일치 여부 확인 */
        checkReviewMemberMatch(review, member);
        /** 이미 삭제된 후기인지 확인 */
        checkDeleteReview(review);

        review.updateReviewStatus(ReviewStatus.DELETE);
        review.updateDeleteDate(LocalDateTime.now());

        return ResponseDto.builder()
            .statusCode(HttpStatus.OK.value())
            .message(CANCEL_MESSAGE)
            .body(DeleteReviewDto.builder()
                .performanceName(review.getReservation().getPerformanceSchedule().getPerformance().getName())
                .title(review.getTitle())
                .writer(review.getMember().getNickname())
                .reviewStatus(review.getReviewStatus().name())
                .deleteDate(review.getDeleteDate())
                .build())
            .build();
    }

    @Transactional
    @Override
    public ResponseDto changeReview(ReviewRequestDto dto, Long reviewId, Authentication auth) {
        String email = auth.getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(NotExistSuitableDataException::new);
        Review review = reviewRepository.findById(reviewId).orElseThrow(NotExistSuitableDataException::new);

        /** 후기 정보의 회원정보와 로그인 회원 일치 여부 확인 */
        checkReviewMemberMatch(review, member);
        /** 삭제된 후기인지 확인 */
        checkDeleteReview(review);

        if (!review.getTitle().equals(dto.getReviewTitle())) {
            review.updateTitle(dto.getReviewTitle());
        }
        if (!review.getContent().equals(dto.getReviewContent())) {
            review.updateContent(dto.getReviewContent());
        }

        review.updateWritingDate(LocalDateTime.now());

        return ResponseDto.builder()
            .statusCode(HttpStatus.OK.value())
            .message(CHANGE_MESSAGE)
            .body(UpdateReviewDto.builder()
                .performanceName(review.getReservation().getPerformanceSchedule().getPerformance().getName())
                .title(review.getTitle())
                .writer(review.getMember().getNickname())
                .reviewStatus(review.getReviewStatus().name())
                .writingDate(review.getWritingDate())
                .build())
            .build();
    }

    @Override
    // 삭제된 후기인지 확인
    public void checkDeleteReview(Review review) {
        if (ReviewStatus.DELETE.equals(review.getReviewStatus())) {
            throw new DeletedReviewException();
        }
    }

    @Override
    // 로그인 회원과 예매를 작성한 회원이 일치하는지 확인
    public void checkReviewMemberMatch(Review review, Member member) {
        if (!review.getMember().equals(member)) {
            throw new NotExistSuitableDataException();
        }
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
