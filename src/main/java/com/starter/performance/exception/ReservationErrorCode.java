package com.starter.performance.exception;

public enum ReservationErrorCode {
    NOT_PRESENT_TICKET_EXCEPTION,           // 예매 가능 티켓 x
    NOT_PROPER_TICKET_NUM_EXCEPTION,        // 예매 티켓 수 제한 (최대 2매)
    NOT_PROPER_RESERVATION_DATE_EXCEPTION,  // 예매 가능한 날짜 x
    NOT_PROPER_PERFORMANCE_STATUS_EXCEPTION,// 공연이 예매 불가능 상태 - performance_status != TICKETING
    EXIST_RESERVATION_EXCEPTION,            // 이미 예매 내역이 있는 상태 -> 예매 불가
    CAN_NOT_VIP_RESERVATION_EXCEPTION,      // VIP 표 없어서 구매 못하는 상황
    NOT_VALID_PERFORMANCE_EXCEPTION,        // 올바른 공연 정보가 아닐 때
    NOT_VALID_MEMBER,                       // 예매한 회원이 아닌 다른 회원이 취소 요청
    CANCELED_RESERVATION_EXCEPTION,         // 취소된 예매 내역을 수정/취소하려고 할 때
}
