package com.starter.performance.exception.impl;

import com.starter.performance.exception.AbstractException;
import com.starter.performance.exception.Data;
import com.starter.performance.exception.ReviewErrorCode;
import org.springframework.http.HttpStatus;

public class OnlyOneReviewException extends AbstractException {

//    private static final String MESSAGE = "예매 한 번 당 오직 한 개의 후기만 작성할 수 있습니다.";
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public Object getData() {
        return Data.builder()
            .errorType(ReviewErrorCode.ONLY_ONE_REVIEW_PER_RESERVATION_EXCEPTION)
//            .errorMessage(MESSAGE)
            .build();
    }
}
