package com.starter.performance.exception;

public enum ReservationErrorCode {
    NOT_PRESENT_TICKET_EXCEPTION,           // 예매 가능 티켓 x
    NOT_PROPER_TICKET_NUM_EXCEPTION,        // 예매 티켓 수 제한 (최대 2매)
    NOT_PROPER_RESERVATION_DATE_EXCEPTION,  // 예매 가능한 날짜 x
    NOT_PROPER_PERFORMANCE_STATUS_EXCEPTION,// 공연이 예매 불가능 상태 - performance_status != TICKETING

}
