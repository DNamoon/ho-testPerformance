package com.starter.performance.exception;

import lombok.Getter;

@Getter
public enum ReviewErrorCode {
    NOT_CREATE_REVIEW_EXCEPTION, // 현재 후기 작성 X
    UNTITLED_EXCEPTION, // 제목 없음
    NO_CONTENT_EXCEPTION // 내용 없음
}
