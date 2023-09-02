package com.starter.performance.exception.impl;

import com.starter.performance.exception.AbstractException;
import com.starter.performance.exception.ErrorData;
import com.starter.performance.exception.ReservationErrorCode;
import org.springframework.http.HttpStatus;

public class NotProperTicketNumException extends AbstractException {

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public ErrorData getData() {
        return new ErrorData(ReservationErrorCode.NOT_PROPER_TICKET_NUM_EXCEPTION.name());
    }
}
