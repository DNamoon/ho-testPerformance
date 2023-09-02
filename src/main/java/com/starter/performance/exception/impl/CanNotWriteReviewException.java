package com.starter.performance.exception.impl;

import com.starter.performance.exception.AbstractException;
import com.starter.performance.exception.ErrorData;
import com.starter.performance.exception.ReviewErrorCode;
import org.springframework.http.HttpStatus;

public class CanNotWriteReviewException extends AbstractException {

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public ErrorData getData() {
        return new ErrorData(ReviewErrorCode.CAN_NOT_WRITE_REVIEW_EXCEPTION.name());
    }

}
