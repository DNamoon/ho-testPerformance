package com.starter.performance.exception.impl;

import com.starter.performance.exception.AbstractException;
import com.starter.performance.exception.ReservationErrorCode;
import org.springframework.http.HttpStatus;

public class NotProperReservationDateException extends AbstractException {

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public Object getData() {
        return ReservationErrorCode.NOT_PROPER_RESERVATION_DATE_EXCEPTION;
    }
}
