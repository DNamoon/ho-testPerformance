package com.starter.performance.exception;

import lombok.Getter;

@Getter
public enum ReviewErrorCode {
    CAN_NOT_WRITE_REVIEW_EXCEPTION, // 현재 후기 작성 할 수 없는 상태
    ONLY_ONE_REVIEW_PER_RESERVATION_EXCEPTION, // 예매 내역 하나당 오직 하나의 후기만
    NOT_MATCH_RESERVATION_AND_MEMBER_EXCEPTION, //예매 내역의 회원정보와 후기 작성 시도 중인 회원 불일치
}
