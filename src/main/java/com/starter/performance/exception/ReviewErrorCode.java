package com.starter.performance.exception;

import lombok.Getter;

@Getter
public enum ReviewErrorCode {
    UNTITLED_EXCEPTION, // 제목 없음
    NO_CONTENT_EXCEPTION, // 내용 없음
    CAN_NOT_WRITE_REVIEW_EXCEPTION, // 현재 후기 작성 할 수 없는 상태
    ONLY_ONE_REVIEW_PER_RESERVATION_EXCEPTION, // 예매 내역 하나당 오직 하나의 후기만
}
