package com.starter.performance.exception.impl;

import com.starter.performance.exception.AbstractException;
import com.starter.performance.exception.Data;
import com.starter.performance.exception.ReviewErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

public class CanNotWriteReviewException extends AbstractException {

//    private static final String MESSAGE = "현재 후기를 작성할 수 없습니다.";
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public Object getData() {
        return Data.builder()
            .errorType(ReviewErrorCode.CAN_NOT_WRITE_REVIEW_EXCEPTION)
//            .errorMessage(MESSAGE)
            .build();
    }

}
