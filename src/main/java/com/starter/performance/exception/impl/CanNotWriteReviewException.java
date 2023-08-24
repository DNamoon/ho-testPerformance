package com.starter.performance.exception.impl;

import com.starter.performance.exception.AbstractException;
import com.starter.performance.exception.Data;
import com.starter.performance.exception.ReviewErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class CanNotWriteReviewException extends AbstractException {

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public Object getData() {
        return Data.builder()
            .errorType(ReviewErrorCode.CAN_NOT_WRITE_REVIEW_EXCEPTION)
            .build();
    }

}